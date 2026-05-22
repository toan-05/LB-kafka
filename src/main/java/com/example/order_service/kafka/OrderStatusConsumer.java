package com.example.order_service.kafka;

import com.example.order_service.config.AppProperties;
import com.example.order_service.event.InventoryResultEvent;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusConsumer.class);

    private final OrderService orderService;
    private final AppProperties appProperties;

    @KafkaListener(
            topics = "${app.kafka.inventory-result-topic}",
            groupId = "order-status-updater"
    )
    public void updateOrderStatus(InventoryResultEvent event) {
        String instanceName = appProperties.getInstanceName();
        if (event.isReserved()) {
            orderService.markInventoryReserved(event.getOrderId(), instanceName);
        } else {
            orderService.markInventoryRejected(event.getOrderId(), event.getReason(), instanceName);
        }

        log.info(
                "Order status updated from Kafka event. orderId={}, reserved={}, updater={}",
                event.getOrderId(),
                event.isReserved(),
                instanceName
        );
    }
}
