package com.azubike.ellpsis.command.interceptor;

import com.azubike.ellpsis.command.CreateProductCommand;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@Component
@Slf4j
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull final List<? extends CommandMessage<?>> list) {
        return (index, command) -> {
            if (command.getPayloadType().equals(CreateProductCommand.class)) {
                log.info("Intercepted {}", command.getPayloadType().toString());
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Product price must be greater than zero");
                }
                if (Objects.requireNonNull(createProductCommand.getTitle()).isBlank() || createProductCommand.getTitle() == null) {
                    throw new IllegalArgumentException("Title cannot be empty");
                }
            }
            return command;
        };
    }
}
