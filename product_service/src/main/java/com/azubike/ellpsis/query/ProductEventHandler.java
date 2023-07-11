package com.azubike.ellpsis.query;

import com.azubike.ellpsis.core.data.ProductEntity;
import com.azubike.ellpsis.core.data.ProductRepository;
import com.azubike.ellpsis.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("product-group")
public class ProductEventHandler {
    final ProductRepository productRepository;
    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent ,productEntity);
        // persists the command object in the main storage
        productRepository.save(productEntity);
    }

}
