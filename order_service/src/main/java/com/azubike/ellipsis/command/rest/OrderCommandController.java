package com.azubike.ellipsis.command.rest;

import com.azubike.ellipsis.command.commands.CreateOrderCommand;
import com.azubike.ellipsis.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderCommandController {
    private final CommandGateway commandGateway;

    @PostMapping
    public String createOrder(@RequestBody @Valid CreateOrderModel createOrderModel) {
        final CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderStatus(OrderStatus.CREATED).addressId(createOrderModel.getAddressId())
                .orderId(UUID.randomUUID().toString()).productId(createOrderModel.getProductId())
                .quantity(createOrderModel.getQuantity())
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b").build();

        return commandGateway.sendAndWait(createOrderCommand);
    }
}
