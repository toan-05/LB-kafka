package com.example.order_service.service.impl;

import com.example.order_service.entity.Product;
import com.example.order_service.entity.InventoryReservation;
import com.example.order_service.entity.enums.RecordStatus;
import com.example.order_service.entity.enums.OrderStatus;
import com.example.order_service.event.InventoryResultEvent;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.mapper.InventoryResultMapper;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.ProductRepository;
import com.example.order_service.repository.InventoryReservationRepository;
import com.example.order_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final InventoryReservationRepository inventoryReservationRepository;
    private final OrderRepository orderRepository;
    private final InventoryResultMapper inventoryResultMapper;

    @Override
    @Transactional
    public InventoryResultEvent reserveInventory(OrderCreatedEvent event, String processedBy) {
        InventoryReservation reservation = inventoryReservationRepository
                .findByOrderId(event.getOrderId())
                .orElse(null);
        if (reservation != null) {
            return inventoryResultMapper.toResultEvent(reservation);
        }

        boolean orderIsReservable = orderRepository
                .findByIdAndStatusAndOrderStatus(event.getOrderId(), RecordStatus.ACTIVE, OrderStatus.CREATED)
                .isPresent();
        if (!orderIsReservable) {
            InventoryResultEvent result = inventoryResultMapper.rejected(
                    event,
                    "Order is not available for inventory reservation",
                    processedBy
            );
            inventoryReservationRepository.save(inventoryResultMapper.toReservation(result));
            return result;
        }

        Product product = productRepository.findByIdForUpdate(event.getProductId(), RecordStatus.ACTIVE).orElse(null);

        InventoryResultEvent result;
        if (product == null) {
            result = inventoryResultMapper.rejected(event, "Product not found", processedBy);
        } else if (product.getStockQuantity() < event.getQuantity()) {
            result = inventoryResultMapper.rejected(event, "Not enough stock", processedBy);
        } else {
            product.setStockQuantity(product.getStockQuantity() - event.getQuantity());
            productRepository.save(product);
            result = inventoryResultMapper.reserved(event, processedBy);
        }

        inventoryReservationRepository.save(inventoryResultMapper.toReservation(result));
        return result;
    }
}
