package com.azubike.ellpisis.query;

import com.azubike.ellipsis.models.PaymentDetails;
import com.azubike.ellipsis.models.User;
import com.azubike.ellipsis.query.FetchUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserQueryHandler {
    @QueryHandler
    public User fetchUser(FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery) {
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("ENU RICHARD")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        return User.builder()
                .firstName("Enu")
                .lastName("Richard")
                .userId(fetchUserPaymentDetailsQuery.getUserId())
                .paymentDetails(paymentDetails)
                .build();
    }
}
