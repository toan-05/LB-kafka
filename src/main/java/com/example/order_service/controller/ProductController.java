package com.example.order_service.controller;

import com.example.order_service.dto.request.CreateProductRequest;
import com.example.order_service.dto.request.UpdateProductRequest;
import com.example.order_service.dto.response.ProductResponse;
import com.example.order_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for product catalog operations.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Creates a new product.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ProductResponse.from(productService.createProduct(request));
    }

    /**
     * Returns one product by id.
     */
    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return ProductResponse.from(productService.getProduct(id));
    }

    /**
     * Returns all products.
     */
    @GetMapping
    public List<ProductResponse> getProducts() {
        return productService.getProducts().stream()
                .map(ProductResponse::from)
                .toList();
    }

    /**
     * Replaces product details.
     */
    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return ProductResponse.from(productService.updateProduct(id, request));
    }

    /**
     * Deletes one product by id.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
