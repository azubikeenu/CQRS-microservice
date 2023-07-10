package com.azubike.ellpsis.rest.controller;

import com.azubike.ellpsis.command.CreateProductCommand;
import com.azubike.ellpsis.rest.dto.CreateProductDto;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final Environment environment;
    private final CommandGateway commandGateway;


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String createProduct(@RequestBody CreateProductDto createProductDto) {
        final CreateProductCommand createProductCommand =
                CreateProductCommand.builder().price(createProductDto.getPrice())
                        .title(createProductDto.getTitle())
                        .quantity(createProductDto.getQuantity())
                        .productId(UUID.randomUUID().toString()).build();
        String returnedValue = "";
        try{
        returnedValue = commandGateway.sendAndWait(createProductCommand);
        }catch(Exception ex){
            returnedValue = ex.getLocalizedMessage();
        }

        return returnedValue;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String getProduct() {
        return "Get product dummy data";
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String updateProduct() {
        return "Update product dummy data";
    }

    @DeleteMapping
    public String deleteProduct() {
        return "Delete Product dummy data";
    }

}
