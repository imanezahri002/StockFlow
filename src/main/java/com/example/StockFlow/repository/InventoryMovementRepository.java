package com.example.StockFlow.repository;

import com.example.StockFlow.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement,Long> {
}
