package com.azubike.ellpsis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final Environment environment;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String createProduct() {
        return "created product successfully " + environment.getProperty("local.server.port");
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
