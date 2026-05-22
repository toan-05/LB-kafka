package com.example.order_service.service.impl;

import com.example.order_service.entity.Order;
import com.example.order_service.entity.Product;
import com.example.order_service.entity.dto.request.CreateOrderRequest;
import com.example.order_service.entity.enums.OrderStatus;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.kafka.publisher.OrderEventPublisher;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.OrderService;
import com.example.order_service.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderEventPublisher orderEventPublisher;
    private final String instanceName;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductService productService,
            OrderEventPublisher orderEventPublisher,
            @Value("${app.instance-name}") String instanceName
    ) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.orderEventPublisher = orderEventPublisher;
        this.instanceName = instanceName;
    }

    @Override
    public Order createOrder(CreateOrderRequest request) {
        Product product = productService.getProduct(request.productId());

        Order order = Order.builder()
                .productId(product.getId())
                .productName(product.getName())
                .quantity(request.quantity())
                .unitPrice(product.getPrice())
                .totalAmount(product.getPrice().multiply(BigDecimal.valueOf(request.quantity())))
                .status(OrderStatus.CREATED)
                .statusReason("Order accepted")
                .handledBy(instanceName)
                .build();

        Order savedOrder = orderRepository.save(order);
        orderEventPublisher.publishOrderCreated(OrderCreatedEvent.from(savedOrder));

        return savedOrder;
    }

    @Override
    public Order getOrder(Long id) {
        return findOrder(id);
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order markInventoryReserved(Long orderId, String processedBy) {
        Order order = findOrder(orderId);
        order.setStatus(OrderStatus.INVENTORY_RESERVED);
        order.setStatusReason("Stock reserved");
        order.setHandledBy(order.getHandledBy() + " | inventory:" + processedBy);
        return orderRepository.save(order);
    }

    @Override
    public Order markInventoryRejected(Long orderId, String reason, String processedBy) {
        Order order = findOrder(orderId);
        order.setStatus(OrderStatus.INVENTORY_REJECTED);
        order.setStatusReason(reason);
        order.setHandledBy(order.getHandledBy() + " | inventory:" + processedBy);
        return orderRepository.save(order);
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id));
    }
}
