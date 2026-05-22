package com.example.order_service.kafka;

import com.example.order_service.event.InventoryResultEvent;
import com.example.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusConsumer {

    private final OrderService orderService;
    private final String instanceName;

    public OrderStatusConsumer(
            OrderService orderService,
            @Value("${app.instance-name}") String instanceName
    ) {
        this.orderService = orderService;
        this.instanceName = instanceName;
    }

    @KafkaListener(
            topics = "${app.kafka.inventory-result-topic}",
            groupId = "order-status-updater"
    )
    public void updateOrderStatus(InventoryResultEvent event) {
        if (event.isReserved()) {
            orderService.markInventoryReserved(event.getOrderId(), instanceName);
        } else {
            orderService.markInventoryRejected(event.getOrderId(), event.getReason(), instanceName);
        }

        System.out.println(
                "Order status updated from Kafka event. orderId=" + event.getOrderId()
                        + ", reserved=" + event.isReserved()
                        + ", updater=" + instanceName
        );
    }
}
