package com.azubike.ellipsis.saga;

import com.azubike.ellipsis.command.commands.ApproveOrderCommand;
import com.azubike.ellipsis.commands.ProcessPaymentCommand;
import com.azubike.ellipsis.commands.ReserveProductCommand;
import com.azubike.ellipsis.core.events.OrderApprovedEvent;
import com.azubike.ellipsis.core.events.OrderCreatedEvent;
import com.azubike.ellipsis.events.PaymentProcessedEvent;
import com.azubike.ellipsis.events.ProductReservedEvent;
import com.azubike.ellipsis.models.User;
import com.azubike.ellipsis.query.FetchUserPaymentDetailsQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
@Component
@Slf4j
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryGateway queryGateway;


    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        final ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder().productId(orderCreatedEvent.getProductId()).orderId(orderCreatedEvent.getOrderId()).userId(orderCreatedEvent.getUserId()).quantity(orderCreatedEvent.getQuantity()).build();
        commandGateway.send(reserveProductCommand, ((commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                // handle compensating transaction
            }
        }));
        log.info("Handling order created event for order : {}", orderCreatedEvent.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        log.info("Handling product reserved event for product : {}", productReservedEvent.getProductId());
        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery();
        fetchUserPaymentDetailsQuery.setUserId(productReservedEvent.getUserId());
        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception ex) {
            ex.printStackTrace();
            // Start compensating transaction
        }
        if (userPaymentDetails == null) {
            // Start compensating transaction
            return;
        }

        log.info("Fetching user details for user : {}", Objects.requireNonNull(userPaymentDetails).getFirstName());

        final ProcessPaymentCommand paymentCommand = ProcessPaymentCommand.builder()
                .paymentDetails(userPaymentDetails.getPaymentDetails()).orderId(productReservedEvent.getOrderId())
                .paymentId(UUID.randomUUID().toString()).build();
        try {
            commandGateway.sendAndWait(paymentCommand, 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("An Error occurred while publishing the process payment command : {}", ex.getMessage());
            // start compensating transaction
        }

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        final ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.sendAndWait(approveOrderCommand);
    }
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        log.info("Order is approved for order id : {}", orderApprovedEvent.getOrderId());
    }
}