package com.azubike.ellpsis.command.interceptor;

import com.azubike.ellpsis.command.commands.CreateProductCommand;
import com.azubike.ellpsis.core.data.ProductLookupEntity;
import com.azubike.ellpsis.core.data.ProductLookupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
    private final ProductLookupRepository productLookupRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull final List<? extends CommandMessage<?>> list) {
        return (index, command) -> {
            // narrow action to the CreateProductCommand
            if (command.getPayloadType().equals(CreateProductCommand.class)) {
                log.info("Intercepted {}", command.getPayloadType().getSimpleName());
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                final ProductLookupEntity foundProduct = productLookupRepository.
                        findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());
                if (foundProduct != null) {
                    throw new IllegalArgumentException(String.format("Product with id %s or title %s already exists",
                            createProductCommand.getProductId(), createProductCommand.getTitle()));
                }

            }
            return command;
        };
    }
}
