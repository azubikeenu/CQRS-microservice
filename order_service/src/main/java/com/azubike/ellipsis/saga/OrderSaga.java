package com.azubike.ellipsis.saga;

import com.azubike.ellipsis.commands.ReserveProductCommand;
import com.azubike.ellipsis.core.events.OrderCreatedEvent;
import com.azubike.ellipsis.events.ProductReservedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;

@Saga
@RequiredArgsConstructor
public class OrderSaga {
    private final transient CommandGateway commandGateway;

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
    }

    @SagaEventHandler(associationProperty ="orderId")
    public void handle(ProductReservedEvent productReservedEvent) {

    }

}
