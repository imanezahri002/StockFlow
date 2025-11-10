package com.example.StockFlow.repository;

import com.example.StockFlow.entity.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine,Long> {
    public List<SalesOrderLine> findSalesOrderLinesByProductSku(String sku);
}
