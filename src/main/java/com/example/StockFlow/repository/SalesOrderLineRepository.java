package com.example.StockFlow.repository;

import com.example.StockFlow.entity.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine,Long> {
}
