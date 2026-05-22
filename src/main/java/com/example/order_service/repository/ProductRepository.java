package com.example.order_service.repository;

import com.example.order_service.entity.Product;
import com.example.order_service.entity.enums.RecordStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndStatus(Long id, RecordStatus status);

    List<Product> findAllByStatus(RecordStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id and p.status = :status")
    Optional<Product> findByIdForUpdate(@Param("id") Long id, @Param("status") RecordStatus status);
}
