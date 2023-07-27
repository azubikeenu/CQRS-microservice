package com.azubike.ellipsis.command;


import com.azubike.ellipsis.commands.ProcessPaymentCommand;
import com.azubike.ellipsis.events.PaymentProcessedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class PaymentAggregate {

    private String orderId;
    @AggregateIdentifier
    private String paymentId;

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
        if(processPaymentCommand.getPaymentDetails() == null) {
            throw new IllegalArgumentException("Missing payment details");
        }

        if(processPaymentCommand.getOrderId() == null) {
            throw new IllegalArgumentException("Missing orderId");
        }

        if(processPaymentCommand.getPaymentId() == null) {
            throw new IllegalArgumentException("Missing paymentId");
        }
        final PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder()
                .orderId(processPaymentCommand.getOrderId())
                .paymentId(processPaymentCommand.getPaymentId()).build();

        System.out.println(paymentProcessedEvent);

        AggregateLifecycle.apply(paymentProcessedEvent);
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        this.paymentId = paymentProcessedEvent.getPaymentId();
        this.orderId = paymentProcessedEvent.getOrderId();
    }
}
