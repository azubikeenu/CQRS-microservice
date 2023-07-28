package com.azubike.ellipsis.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Data
@Builder
public class CancelProductReservationCommand {
    @TargetAggregateIdentifier
    private final String productId ;
    private final String orderId;
    private final String userId;
    private final Integer quantity;
    private final String reason;
}
