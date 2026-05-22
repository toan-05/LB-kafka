package com.example.order_service.controller;

import com.example.order_service.dto.response.InventoryReservationResponse;
import com.example.order_service.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for checking and cleaning inventory reservations.
 */
@RestController
@RequestMapping("/api/v1/inventory-reservations")
@RequiredArgsConstructor
public class InventoryReservationController {

    private final InventoryReservationService inventoryReservationService;

    /**
     * Returns one inventory reservation by order id.
     */
    @GetMapping("/{orderId}")
    public InventoryReservationResponse getReservation(@PathVariable Long orderId) {
        return InventoryReservationResponse.from(inventoryReservationService.getReservation(orderId));
    }

    /**
     * Returns all inventory reservations.
     */
    @GetMapping
    public List<InventoryReservationResponse> getReservations() {
        return inventoryReservationService.getReservations().stream()
                .map(InventoryReservationResponse::from)
                .toList();
    }

    /**
     * Deletes one inventory reservation by order id.
     */
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable Long orderId) {
        inventoryReservationService.deleteReservation(orderId);
    }
}
