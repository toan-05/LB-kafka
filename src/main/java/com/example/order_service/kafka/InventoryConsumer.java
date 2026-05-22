package com.example.order_service.kafka;

import com.example.order_service.config.AppProperties;
import com.example.order_service.event.InventoryResultEvent;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.kafka.publisher.InventoryEventPublisher;
import com.example.order_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryConsumer.class);

    private final InventoryService inventoryService;
    private final InventoryEventPublisher inventoryEventPublisher;
    private final AppProperties appProperties;

    @KafkaListener(
            topics = "${app.kafka.order-created-topic}",
            groupId = "inventory-service"
    )
    public void reserveInventory(OrderCreatedEvent event) {
        String instanceName = appProperties.getInstanceName();
        InventoryResultEvent result = inventoryService.reserveInventory(event, instanceName);
        inventoryEventPublisher.publishInventoryResult(result);

        log.info(
                "Inventory processed orderId={}, quantity={}, reserved={}, processor={}",
                event.getOrderId(),
                event.getQuantity(),
                result.isReserved(),
                instanceName
        );
    }
}
