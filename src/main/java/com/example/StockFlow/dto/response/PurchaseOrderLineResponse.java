package com.example.StockFlow.dto.response;

import lombok.Data;

@Data
public class PurchaseOrderLineResponse {
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
}
