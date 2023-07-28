package com.azubike.ellipsis.command.commands;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class RejectOrderCommand {
    @TargetAggregateIdentifier
    private final String orderId;
    private final String reason;
}
