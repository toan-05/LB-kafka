package com.example.order_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppProperties {

    @Value("${app.instance-name}")
    private String instanceName;

    @Value("${app.kafka.order-created-topic}")
    private String orderCreatedTopic;

    @Value("${app.kafka.inventory-result-topic}")
    private String inventoryResultTopic;
}
