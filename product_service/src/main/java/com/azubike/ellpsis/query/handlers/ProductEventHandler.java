package com.azubike.ellpsis.query.handlers;

import com.azubike.ellipsis.events.ProductReservationCancelledEvent;
import com.azubike.ellipsis.events.ProductReservedEvent;
import com.azubike.ellpsis.core.data.ProductEntity;
import com.azubike.ellpsis.core.data.ProductRepository;
import com.azubike.ellpsis.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("product-group")
@Slf4j
public class ProductEventHandler {
    final ProductRepository productRepository;

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent, productEntity);
        try {
            // persists the command object in the main storage
            final ProductEntity savedProduct = productRepository.save(productEntity);
            log.info("Saving product {} with id {}", savedProduct.getTitle(), savedProduct.getProductId());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

    }


    @EventHandler
    public void reserveProduct(ProductReservedEvent productReservedEvent) {
        final ProductEntity foundProduct = productRepository.findByProductId(productReservedEvent.getProductId());
        log.info("Product quantity : {} for product {} ", foundProduct.getQuantity(), foundProduct.getProductId());
        foundProduct.setQuantity(foundProduct.getQuantity() - productReservedEvent.getQuantity());
        log.info("Reserving quantity {} for product {}", productReservedEvent.getQuantity(), foundProduct.getProductId());
        final ProductEntity productEntity = productRepository.save(foundProduct);
        log.info("New product : quantity {} for product {}", productEntity.getQuantity(), productEntity.getProductId());

    }

    @EventHandler
    public void cancelReservation(ProductReservationCancelledEvent productReservationCancelledEvent) {
        final ProductEntity storedProduct = productRepository.findByProductId(productReservationCancelledEvent.getProductId());
        log.info("Product quantity : {} for product {} ", storedProduct.getQuantity(), storedProduct.getProductId());
        var newQuantity = storedProduct.getQuantity() + productReservationCancelledEvent.getQuantity();
        storedProduct.setQuantity(newQuantity);
        log.info("Cancelling reservation  for product {}", storedProduct.getProductId());
        final ProductEntity productEntity = productRepository.save(storedProduct);
        log.info("New product : quantity {} for product {}", productEntity.getQuantity(), productEntity.getProductId());
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException ex) {
        throw ex;
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception ex) throws Exception {
        throw ex;
    }

}
