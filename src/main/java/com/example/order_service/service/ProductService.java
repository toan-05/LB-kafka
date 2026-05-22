package com.example.order_service.service;

import com.example.order_service.entity.dto.request.CreateProductRequest;
import com.example.order_service.entity.Product;

import java.util.List;

public interface ProductService {

    Product createProduct(CreateProductRequest request);

    Product getProduct(Long id);

    List<Product> getProducts();
}
