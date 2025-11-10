package com.example.StockFlow.repository;

import com.example.StockFlow.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesOrderRepository extends JpaRepository<SalesOrder,Long> {

}
