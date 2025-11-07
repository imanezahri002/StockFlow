package com.example.StockFlow.repository;

import com.example.StockFlow.entity.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine,Long> {
}