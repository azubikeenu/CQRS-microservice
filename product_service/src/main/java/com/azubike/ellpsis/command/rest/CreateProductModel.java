package com.azubike.ellpsis.command.rest;


import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class CreateProductModel {
    private String title;
    private BigDecimal price ;
    private Integer quantity;
}
