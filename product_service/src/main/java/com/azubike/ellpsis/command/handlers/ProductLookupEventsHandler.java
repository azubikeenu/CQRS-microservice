package com.azubike.ellpsis.command.handlers;

import com.azubike.ellpsis.core.data.ProductLookupEntity;
import com.azubike.ellpsis.core.data.ProductLookupRepository;
import com.azubike.ellpsis.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {
    final ProductLookupRepository productLookupRepository;
    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        ProductLookupEntity productLookupEntity = new ProductLookupEntity(productCreatedEvent.getProductId(),
                productCreatedEvent.getTitle());
        productLookupRepository.save(productLookupEntity);
    }

}
