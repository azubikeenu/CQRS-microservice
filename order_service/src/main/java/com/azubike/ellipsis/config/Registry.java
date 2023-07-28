package com.azubike.ellipsis.config;

import com.azubike.ellipsis.core.exception_handling.OrderServiceEventExceptionHandler;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Registry {
    @Autowired
    public void configure(EventProcessingConfigurer configurer) {
        configurer.registerListenerInvocationErrorHandler("order-group", configuration ->
                new OrderServiceEventExceptionHandler()
        );
    }
}