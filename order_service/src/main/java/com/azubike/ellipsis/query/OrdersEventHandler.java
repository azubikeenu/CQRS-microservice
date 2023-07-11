package com.azubike.ellipsis.query;

import com.azubike.ellipsis.core.events.OrderCreatedEvent;
import com.azubike.ellipsis.core.OrderEntity;
import com.azubike.ellipsis.core.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrdersEventHandler {
    private final OrderRepository orderRepository;

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(orderCreatedEvent, orderEntity);
        orderRepository.save(orderEntity);
        log.info("Successfully saved order with id {}" ,orderEntity.getOrderId());
    }
}
