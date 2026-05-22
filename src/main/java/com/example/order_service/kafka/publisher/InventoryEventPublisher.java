package com.example.order_service.kafka.publisher;

import com.example.order_service.event.InventoryResultEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String inventoryResultTopic;

    public InventoryEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.inventory-result-topic}") String inventoryResultTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryResultTopic = inventoryResultTopic;
    }

    public void publishInventoryResult(InventoryResultEvent event) {
        kafkaTemplate.send(inventoryResultTopic, String.valueOf(event.getOrderId()), event);
    }
}
