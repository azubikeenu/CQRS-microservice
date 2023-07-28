package com.azubike.ellipsis.query;

import com.azubike.ellipsis.core.OrderEntity;
import com.azubike.ellipsis.core.OrderRepository;
import com.azubike.ellipsis.core.events.OrderApprovedEvent;
import com.azubike.ellipsis.core.events.OrderCreatedEvent;
import com.azubike.ellipsis.core.events.OrderRejectedEvent;
import com.azubike.ellipsis.core.exception_handling.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ProcessingGroup("order-group")
public class OrdersEventHandler {
    private final OrderRepository orderRepository;

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(orderCreatedEvent, orderEntity);
        orderRepository.save(orderEntity);
        log.info("Successfully saved order with id {}", orderEntity.getOrderId());
    }

    @EventHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {
        final OrderEntity orderEntity = orderRepository.findByOrderId(orderApprovedEvent.getOrderId());
        if (orderEntity == null) {
            throw new OrderNotFoundException(String.format("order with id %s does not exist", orderApprovedEvent.getOrderId()));
        }
        orderEntity.setOrderStatus(orderApprovedEvent.getOrderStatus());
        orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderRejectedEvent orderRejectedEvent) {
        final OrderEntity orderEntity = orderRepository.findByOrderId(orderRejectedEvent.getOrderId());
        if (orderEntity == null) {
            throw new OrderNotFoundException(String.format("order with id %s does not exist", orderRejectedEvent.getOrderId()));
        }
        orderEntity.setOrderStatus(orderRejectedEvent.getOrderStatus());
        orderRepository.save(orderEntity);
    }

    @ExceptionHandler(resultType = OrderNotFoundException.class)
    public void handle(OrderNotFoundException ex) {
        throw ex;
    }
}