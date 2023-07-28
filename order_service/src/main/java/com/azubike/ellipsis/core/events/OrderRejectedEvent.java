package com.azubike.ellipsis.core.events;

import com.azubike.ellipsis.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRejectedEvent {
    private final String orderId ;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
