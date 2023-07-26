package com.azubike.ellipsis.saga;

import com.azubike.ellipsis.commands.ReserveProductCommand;
import com.azubike.ellipsis.core.events.OrderCreatedEvent;
import com.azubike.ellipsis.events.ProductReservedEvent;
import com.azubike.ellipsis.models.User;
import com.azubike.ellipsis.query.FetchUserPaymentDetailsQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
        final ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder().
                productId(orderCreatedEvent.getProductId()).orderId(orderCreatedEvent.getOrderId())
                .userId(orderCreatedEvent.getUserId()).quantity(orderCreatedEvent.getQuantity()).build();
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
            userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery,
                    ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            //Start compensating transaction
        }
        log.info("Fetching user details for user : {}", Objects.requireNonNull(userPaymentDetails).getFirstName());


    }

}
