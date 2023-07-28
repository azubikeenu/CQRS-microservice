package com.azubike.ellipsis.events;

import lombok.Data;

@Data
public class ProductReservationCancelledEvent {
    private String productId;
    private String orderId;
    private String userId;
    private Integer quantity;
    private String reason;
}
