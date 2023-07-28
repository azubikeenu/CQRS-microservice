package com.azubike.ellipsis.query;

import com.azubike.ellipsis.core.data.PaymentEntity;
import com.azubike.ellipsis.core.data.PaymentRepository;
import com.azubike.ellipsis.events.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final PaymentRepository paymentRepository;

    @EventHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        PaymentEntity paymentEntity = PaymentEntity.builder().paymentId(paymentProcessedEvent.getPaymentId())
                .orderId(paymentProcessedEvent.getOrderId()).build();
        paymentRepository.save(paymentEntity);
    }


}
