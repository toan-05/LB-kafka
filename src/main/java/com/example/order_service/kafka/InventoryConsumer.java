package com.example.order_service.kafka;

import com.example.order_service.event.InventoryResultEvent;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.kafka.publisher.InventoryEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    private static final int AVAILABLE_QUANTITY = 10;

    private final InventoryEventPublisher inventoryEventPublisher;
    private final String instanceName;

    public InventoryConsumer(
            InventoryEventPublisher inventoryEventPublisher,
            @Value("${app.instance-name}") String instanceName
    ) {
        this.inventoryEventPublisher = inventoryEventPublisher;
        this.instanceName = instanceName;
    }

    @KafkaListener(
            topics = "${app.kafka.order-created-topic}",
            groupId = "inventory-service"
    )
    public void reserveInventory(OrderCreatedEvent event) {
        boolean reserved = event.getQuantity() != null
                && event.getQuantity() > 0
                && event.getQuantity() <= AVAILABLE_QUANTITY;

        InventoryResultEvent result = InventoryResultEvent.builder()
                .orderId(event.getOrderId())
                .productName(event.getProductName())
                .requestedQuantity(event.getQuantity())
                .reserved(reserved)
                .reason(reserved ? "Stock reserved" : "Not enough stock")
                .processedByInstance(instanceName)
                .build();

        inventoryEventPublisher.publishInventoryResult(result);

        System.out.println(
                "Inventory processed orderId=" + event.getOrderId()
                        + ", quantity=" + event.getQuantity()
                        + ", reserved=" + reserved
                        + ", processor=" + instanceName
        );
    }
}
