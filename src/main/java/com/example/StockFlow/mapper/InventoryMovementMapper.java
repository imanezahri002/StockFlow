package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.InventoryMovementRequest;
import com.example.StockFlow.dto.response.InventoryMovementResponse;
import com.example.StockFlow.entity.Inventory;
import com.example.StockFlow.entity.InventoryMovement;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMovementMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inventory", source = "inventory")
    @Mapping(target = "occurredAt", expression = "java(request.getOccurredAt() != null ? request.getOccurredAt() : java.time.LocalDateTime.now())")
    InventoryMovement toEntity(InventoryMovementRequest request, Inventory inventory);


    @Mapping(target = "inventoryId", source = "inventory.id")
    @Mapping(target = "inventoryName", source = "inventory.name")
    InventoryMovementResponse toResponse(InventoryMovement entity);
}
