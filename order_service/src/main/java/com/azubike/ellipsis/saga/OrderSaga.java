package com.azubike.ellipsis.saga;

import com.azubike.ellipsis.command.commands.ApproveOrderCommand;
import com.azubike.ellipsis.command.commands.RejectOrderCommand;
import com.azubike.ellipsis.commands.CancelProductReservationCommand;
import com.azubike.ellipsis.commands.ProcessPaymentCommand;
import com.azubike.ellipsis.commands.ReserveProductCommand;
import com.azubike.ellipsis.core.events.OrderApprovedEvent;
import com.azubike.ellipsis.core.events.OrderCreatedEvent;
import com.azubike.ellipsis.core.events.OrderRejectedEvent;
import com.azubike.ellipsis.events.PaymentProcessedEvent;
import com.azubike.ellipsis.events.ProductReservationCancelledEvent;
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
        final ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder().productId(orderCreatedEvent.getProductId()).
                orderId(orderCreatedEvent.getOrderId()).userId(orderCreatedEvent.getUserId()).quantity(orderCreatedEvent.getQuantity()).build();
        commandGateway.send(reserveProductCommand, ((commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                // handle compensating transaction
                log.error("Error occurred while creating order  with id {}" ,reserveProductCommand.getOrderId());
                RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder().orderId(orderCreatedEvent.getOrderId()).
                        reason(commandResultMessage.toString()).build();
                commandGateway.send(rejectOrderCommand);
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
            log.error("An error occurred while fetching userPaymentDetails :{}", ex.getMessage());
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, ex.getMessage());
        }
        if (userPaymentDetails == null) {
            // Start compensating transaction
            final String reason = "userPaymentDetails returned a null value";
            log.error(reason);
            cancelProductReservation(productReservedEvent, reason);
            return;
        }

        log.info("Fetching user details for user : {}", Objects.requireNonNull(userPaymentDetails).getFirstName());

        final ProcessPaymentCommand paymentCommand = ProcessPaymentCommand.builder()
                .paymentDetails(userPaymentDetails.getPaymentDetails()).orderId(productReservedEvent.getOrderId())
                .paymentId(UUID.randomUUID().toString()).build();
        String result;
        try {
            result = commandGateway.sendAndWait(paymentCommand, 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("An Error occurred while publishing the process payment command : {}", ex.getMessage());
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
            // start compensating transaction
        }
        if (result == null) {
            final String reason = "Process payment command resulted in a NULL value";
            log.error(reason);
            cancelProductReservation(productReservedEvent, reason);
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


    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder().orderId(productReservationCancelledEvent.getOrderId()).
                reason(productReservationCancelledEvent.getReason()).build();
        log.info("Rejecting order with id :{}" ,rejectOrderCommand.getOrderId());
        commandGateway.send(rejectOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderRejectedEvent orderRejectedEvent){
        log.info("Order with id {} was successfully rejected" ,orderRejectedEvent.getOrderId());
    }


    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .productId(productReservedEvent.getProductId())
                .reason(reason)
                .quantity(productReservedEvent.getQuantity())
                .orderId(productReservedEvent.getOrderId())
                .userId(productReservedEvent.getUserId())
                .build();
        commandGateway.send(cancelProductReservationCommand);
    }


}