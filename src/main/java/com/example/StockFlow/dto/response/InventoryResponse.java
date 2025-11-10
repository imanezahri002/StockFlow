package com.example.StockFlow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    private Long id;

    private Long warehouseId;
    private String warehouseName;

    private Long productId;
    private String productName;
    private String productSku;

    private Integer qtyOnHand;
    private Integer qtyReserved;
}
