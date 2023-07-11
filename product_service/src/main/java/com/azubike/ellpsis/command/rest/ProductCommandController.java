package com.azubike.ellpsis.command.rest;

import com.azubike.ellpsis.command.commands.CreateProductCommand;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final Environment environment;
    private final CommandGateway commandGateway;


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createProduct(@Valid @RequestBody CreateProductModel createProductModel) {
        final CreateProductCommand createProductCommand =
                CreateProductCommand.builder().price(createProductModel.getPrice())
                        .title(createProductModel.getTitle())
                        .quantity(createProductModel.getQuantity())
                        .productId(UUID.randomUUID().toString()).build();
        String returnedValue = commandGateway.sendAndWait(createProductCommand);
        return ResponseEntity.ok(returnedValue);
    }


}
