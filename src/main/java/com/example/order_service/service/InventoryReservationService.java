package com.example.order_service.service;

import com.example.order_service.entity.InventoryReservation;

import java.util.List;

/**
 * Handles read and cleanup operations for inventory reservations.
 */
public interface InventoryReservationService {

    /**
     * Returns one inventory reservation by order id.
     */
    InventoryReservation getReservation(Long orderId);

    /**
     * Returns all inventory reservations.
     */
    List<InventoryReservation> getReservations();

    /**
     * Deletes one inventory reservation by order id.
     */
    void deleteReservation(Long orderId);
}
