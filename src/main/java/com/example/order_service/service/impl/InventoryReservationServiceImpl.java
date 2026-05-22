package com.example.order_service.service.impl;

import com.example.order_service.entity.InventoryReservation;
import com.example.order_service.entity.enums.RecordStatus;
import com.example.order_service.exception.ErrorCode;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.repository.InventoryReservationRepository;
import com.example.order_service.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryReservationServiceImpl implements InventoryReservationService {

    private final InventoryReservationRepository inventoryReservationRepository;

    @Override
    public InventoryReservation getReservation(Long orderId) {
        return inventoryReservationRepository.findByOrderIdAndStatus(orderId, RecordStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.INVENTORY_RESERVATION_NOT_FOUND,
                        "Inventory reservation",
                        orderId
                ));
    }

    @Override
    public List<InventoryReservation> getReservations() {
        return inventoryReservationRepository.findAllByStatus(RecordStatus.ACTIVE);
    }

    @Override
    public void deleteReservation(Long orderId) {
        InventoryReservation reservation = getReservation(orderId);
        reservation.markDeleted();
        inventoryReservationRepository.save(reservation);
    }
}
