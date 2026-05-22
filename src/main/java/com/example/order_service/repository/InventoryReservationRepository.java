package com.example.order_service.repository;

import com.example.order_service.entity.InventoryReservation;
import com.example.order_service.entity.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {

    Optional<InventoryReservation> findByOrderId(Long orderId);

    Optional<InventoryReservation> findByOrderIdAndStatus(Long orderId, RecordStatus status);

    List<InventoryReservation> findAllByStatus(RecordStatus status);

    boolean existsByOrderId(Long orderId);

    boolean existsByOrderIdAndStatus(Long orderId, RecordStatus status);
}
