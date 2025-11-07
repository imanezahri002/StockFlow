package com.example.StockFlow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SalesOrder salesOrder;

    @ManyToOne
    private Product product;

    private Integer quantity;
    private BigDecimal unitPrice;
    private boolean backorder;
}

