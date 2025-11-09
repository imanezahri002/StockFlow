package com.example.StockFlow.dto.request;


import lombok.Data;

@Data
public class SalesOrderLineRequest {
    private Long productId;
    private Integer quantity;
}
