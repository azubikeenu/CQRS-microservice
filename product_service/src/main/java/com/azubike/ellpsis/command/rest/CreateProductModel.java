package com.azubike.ellpsis.command.rest;


import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@ToString
public class CreateProductModel {
    //@NotBlank(message = "title cannot be blank")
    private String title;
    @Min(value = 1 , message = "price cannot be lower than 1")
    private BigDecimal price ;
    @Min(value = 1 , message = "quantity must be greater than 1")
    @Max(value = 5 , message = "quantity must be less than 1")
    private Integer quantity;
}
