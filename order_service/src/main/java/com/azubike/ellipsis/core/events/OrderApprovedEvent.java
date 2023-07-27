package com.azubike.ellipsis.core.events;

import com.azubike.ellipsis.model.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {
    String orderId;
    final OrderStatus orderStatus = OrderStatus.APPROVED;

}


