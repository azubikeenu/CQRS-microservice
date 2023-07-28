package com.azubike.ellpsis.command;

import com.azubike.ellipsis.commands.CancelProductReservationCommand;
import com.azubike.ellipsis.commands.ReserveProductCommand;
import com.azubike.ellipsis.events.ProductReservationCancelledEvent;
import com.azubike.ellipsis.events.ProductReservedEvent;
import com.azubike.ellpsis.command.commands.CreateProductCommand;
import com.azubike.ellpsis.core.events.ProductCreatedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Aggregate
@NoArgsConstructor
@Slf4j
public class ProductAggregate {
    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;


    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {

        if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
        if (Objects.requireNonNull(createProductCommand.getTitle()).isBlank() || createProductCommand.getTitle() == null) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);
        // persist the command object in the event store and dispatches event to all eventHandlers listening to the productCreatedEvent
        AggregateLifecycle.apply(productCreatedEvent);
    }


    @CommandHandler
    public void handle(ReserveProductCommand reserveProductCommand) {
        if (quantity < reserveProductCommand.getQuantity()) {
            throw new IllegalArgumentException("Not enough quantity in stock");
        }

        ProductReservedEvent productReservedEvent = new ProductReservedEvent();

        BeanUtils.copyProperties(reserveProductCommand, productReservedEvent);
        AggregateLifecycle.apply(productReservedEvent);
    }

    // This updates the aggregateState with new information

    @CommandHandler
    public void handle(CancelProductReservationCommand cancelProductReservationCommand) {
        ProductReservationCancelledEvent productReservationCancelledEvent = new ProductReservationCancelledEvent();
        BeanUtils.copyProperties(cancelProductReservationCommand, productReservationCancelledEvent);
        AggregateLifecycle.apply(productReservationCancelledEvent);

    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        this.productId = productCreatedEvent.getProductId();
        this.price = productCreatedEvent.getPrice();
        this.quantity = productCreatedEvent.getQuantity();
        this.title = productCreatedEvent.getTitle();
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent productReservedEvent) {
        this.quantity -= productReservedEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
        this.quantity += productReservationCancelledEvent.getQuantity();
    }
}
