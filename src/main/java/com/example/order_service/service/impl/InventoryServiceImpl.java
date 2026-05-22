package com.example.order_service.service.impl;

import com.example.order_service.entity.Product;
import com.example.order_service.event.InventoryResultEvent;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.repository.ProductRepository;
import com.example.order_service.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;

    public InventoryServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public InventoryResultEvent reserveInventory(OrderCreatedEvent event, String processedBy) {
        Product product = productRepository.findByIdForUpdate(event.getProductId()).orElse(null);

        if (product == null) {
            return rejected(event, "Product not found", processedBy);
        }

        if (product.getStockQuantity() < event.getQuantity()) {
            return rejected(event, "Not enough stock", processedBy);
        }

        product.setStockQuantity(product.getStockQuantity() - event.getQuantity());
        productRepository.save(product);

        return InventoryResultEvent.builder()
                .orderId(event.getOrderId())
                .productId(event.getProductId())
                .productName(event.getProductName())
                .requestedQuantity(event.getQuantity())
                .reserved(true)
                .reason("Stock reserved")
                .processedByInstance(processedBy)
                .build();
    }

    private InventoryResultEvent rejected(OrderCreatedEvent event, String reason, String processedBy) {
        return InventoryResultEvent.builder()
                .orderId(event.getOrderId())
                .productId(event.getProductId())
                .productName(event.getProductName())
                .requestedQuantity(event.getQuantity())
                .reserved(false)
                .reason(reason)
                .processedByInstance(processedBy)
                .build();
    }
}
