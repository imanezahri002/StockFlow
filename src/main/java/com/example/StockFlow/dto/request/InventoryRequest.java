package com.example.StockFlow.dto.request;

import lombok.Data;

@Data
public class InventoryRequest {
    private Long warehouseId;
    private Long productId;
    private Integer qtyOnHand;
    private Integer qtyReserved;
}
