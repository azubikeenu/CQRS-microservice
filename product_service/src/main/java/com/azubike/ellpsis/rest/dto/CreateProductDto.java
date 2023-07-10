package com.azubike.ellpsis.rest.dto;


import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class CreateProductDto {
    private String title;
    private BigDecimal price ;
    private Integer quantity;
}
