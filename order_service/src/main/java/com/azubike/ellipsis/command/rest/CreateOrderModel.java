package com.azubike.ellipsis.command.rest;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class CreateOrderModel {

    @NotBlank(message = "must provide a productId")
    private String productId;
    @NotBlank(message = "must provide an addressId")
    private String addressId;
    @Min(value = 0, message = "quantity must be greater than 0")
    @Max(value = 10, message = "quantity must be less than 10")
    private int quantity;
}
