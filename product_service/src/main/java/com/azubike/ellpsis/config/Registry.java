package com.azubike.ellpsis.config;

import com.azubike.ellpsis.command.interceptor.CreateProductCommandInterceptor;
import com.azubike.ellpsis.core.error_handling.ProductServiceEventErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Registry {
    @Autowired
    public void registerCreateProductCommandInterceptor(ApplicationContext applicationContext, CommandBus commandBus) {
        final CreateProductCommandInterceptor commandInterceptor = applicationContext.getBean(CreateProductCommandInterceptor.class);
        commandBus.registerDispatchInterceptor(commandInterceptor);
    }

    @Autowired
    public void configure(EventProcessingConfigurer configurer) {
        configurer.registerListenerInvocationErrorHandler("product-group", configuration ->
                new ProductServiceEventErrorHandler()
        );

////////////////// Using the PropagatingErrorHandler ////////////////////////////////////////
//        configurer.registerListenerInvocationErrorHandler("product-group", configuration ->
//                PropagatingErrorHandler.instance()
//        );
    }
}
