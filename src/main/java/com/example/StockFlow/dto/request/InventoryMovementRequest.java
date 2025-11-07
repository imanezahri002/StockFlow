package com.example.StockFlow.dto.request;

import com.example.StockFlow.entity.enums.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementRequest {

    @NotNull(message = "Movement type is required")
    private MovementType type;

    @NotNull(message = "Inventory ID is required")
    private Long inventoryId;
    @NotNull(message = "Inventory Name is required")
    private String inventoryName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer qty;

    private LocalDateTime occurredAt;

    private String referenceDocument;

    private String description;

}