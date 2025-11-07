package com.example.StockFlow.dto.response;

import com.example.StockFlow.entity.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementResponse {

    private Long id;
    private MovementType type;
    private Long inventoryId;

    private String inventoryName; // Example additional field from Inventory
    private Integer qty;
    private LocalDateTime occurredAt;
    private String referenceDocument;
    private String description;

}
