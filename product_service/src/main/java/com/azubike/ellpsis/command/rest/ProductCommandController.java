package com.azubike.ellpsis.command.rest;

import com.azubike.ellpsis.command.CreateProductCommand;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final Environment environment;
    private final CommandGateway commandGateway;


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String createProduct(@Valid  @RequestBody CreateProductModel createProductModel) {
        final CreateProductCommand createProductCommand =
                CreateProductCommand.builder().price(createProductModel.getPrice())
                        .title(createProductModel.getTitle())
                        .quantity(createProductModel.getQuantity())
                        .productId(UUID.randomUUID().toString()).build();
        String returnedValue = "";
        try{
            //sends the command object to the eventBus/commandGateway
        returnedValue = commandGateway.sendAndWait(createProductCommand);
        }catch(Exception ex){
            returnedValue = ex.getLocalizedMessage();
        }

        return returnedValue;
    }


}
