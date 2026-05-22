package com.example.order_service.repository;

import com.example.order_service.entity.Order;
import com.example.order_service.entity.enums.OrderStatus;
import com.example.order_service.entity.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndStatus(Long id, RecordStatus status);

    Optional<Order> findByIdAndStatusAndOrderStatus(Long id, RecordStatus status, OrderStatus orderStatus);

    List<Order> findAllByStatus(RecordStatus status);
}
