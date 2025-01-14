package com.example.demo.repository;

import com.example.demo.model.InventoryTransaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {
    Optional<InventoryTransaction> findTopByProductIdOrderByTransactionDateDesc(Long productId);
    @Transactional
    void deleteByProductId(Long productId);
}
