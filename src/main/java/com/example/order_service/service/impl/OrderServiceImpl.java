package com.example.order_service.service.impl;

import com.example.order_service.config.AppProperties;
import com.example.order_service.entity.Order;
import com.example.order_service.entity.Product;
import com.example.order_service.dto.request.CreateOrderRequest;
import com.example.order_service.entity.enums.OrderStatus;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.exception.ErrorCode;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.kafka.publisher.OrderEventPublisher;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.OrderService;
import com.example.order_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderMapper orderMapper;
    private final AppProperties appProperties;

    @Override
    public Order createOrder(CreateOrderRequest request) {
        Product product = productService.getProduct(request.productId());

        Order order = orderMapper.toOrder(request, product, appProperties.getInstanceName());
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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND, "Order", id));
    }
}
