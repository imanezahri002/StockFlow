package com.example.StockFlow.dto.request;


import lombok.Data;

@Data
public class PurchaseOrderLineRequest {
    private Long productId;
    private Integer quantity;
    private Double unitPrice;
}
