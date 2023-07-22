package com.azubike.ellpsis.query.handlers;

import com.azubike.ellpsis.core.data.ProductEntity;
import com.azubike.ellpsis.core.data.ProductRepository;
import com.azubike.ellpsis.query.FindProductsQuery;
import com.azubike.ellpsis.query.rest.ProductModel;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductQueryHandler {
    final ProductRepository productRepository;

    @QueryHandler
    public List<ProductModel> findProducts(FindProductsQuery findProductsQuery) {
        final List<ProductEntity> storedProducts = productRepository.findAll();
        List<ProductModel> products = new ArrayList<>();
        for (ProductEntity productEntity : storedProducts) {
            ProductModel productModel = new ProductModel();
            BeanUtils.copyProperties(productEntity, productModel);
            products.add(productModel);
        }
        return products;
    }
}
