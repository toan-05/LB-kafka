package com.example.order_service.service;

import com.example.order_service.dto.request.CreateProductRequest;
import com.example.order_service.dto.request.UpdateProductRequest;
import com.example.order_service.entity.Product;

import java.util.List;

/**
 * Handles product catalog operations.
 */
public interface ProductService {

    /**
     * Creates a new product.
     */
    Product createProduct(CreateProductRequest request);

    /**
     * Returns one product by id.
     */
    Product getProduct(Long id);

    /**
     * Returns all products.
     */
    List<Product> getProducts();

    /**
     * Replaces product details.
     */
    Product updateProduct(Long id, UpdateProductRequest request);

    /**
     * Deletes a product by id.
     */
    void deleteProduct(Long id);
}
