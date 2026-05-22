package com.example.order_service.service.impl;

import com.example.order_service.entity.InventoryReservation;
import com.example.order_service.entity.Product;
import com.example.order_service.event.InventoryResultEvent;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.mapper.InventoryResultMapper;
import com.example.order_service.repository.InventoryReservationRepository;
import com.example.order_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryReservationRepository inventoryReservationRepository;

    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl(
                productRepository,
                inventoryReservationRepository,
                new InventoryResultMapper()
        );
    }

    @Test
    void reserveInventoryDeductsStockAndStoresReservation() {
        OrderCreatedEvent event = orderCreatedEvent();
        Product product = Product.builder()
                .id(10L)
                .name("Keyboard")
                .price(BigDecimal.valueOf(99))
                .stockQuantity(5)
                .build();
        when(inventoryReservationRepository.findById(1L)).thenReturn(Optional.empty());
        when(productRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(product));

        InventoryResultEvent result = inventoryService.reserveInventory(event, "app1");

        assertThat(result.isReserved()).isTrue();
        assertThat(product.getStockQuantity()).isEqualTo(3);
        verify(productRepository).save(product);

        ArgumentCaptor<InventoryReservation> captor = ArgumentCaptor.forClass(InventoryReservation.class);
        verify(inventoryReservationRepository).save(captor.capture());
        assertThat(captor.getValue().getOrderId()).isEqualTo(1L);
        assertThat(captor.getValue().getQuantity()).isEqualTo(2);
        assertThat(captor.getValue().isReserved()).isTrue();
    }

    @Test
    void reserveInventoryReturnsStoredResultForDuplicateEvent() {
        InventoryReservation reservation = InventoryReservation.builder()
                .orderId(1L)
                .productId(10L)
                .productName("Keyboard")
                .quantity(2)
                .reserved(true)
                .reason("Stock reserved")
                .processedByInstance("app1")
                .build();
        when(inventoryReservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        InventoryResultEvent result = inventoryService.reserveInventory(orderCreatedEvent(), "app2");

        assertThat(result.isReserved()).isTrue();
        assertThat(result.getProcessedByInstance()).isEqualTo("app1");
        verify(productRepository, never()).findByIdForUpdate(10L);
        verify(productRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private OrderCreatedEvent orderCreatedEvent() {
        return OrderCreatedEvent.builder()
                .orderId(1L)
                .productId(10L)
                .productName("Keyboard")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(99))
                .totalAmount(BigDecimal.valueOf(198))
                .createdByInstance("app1")
                .build();
    }
}
