package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.InventoryRequest;
import com.example.StockFlow.dto.response.InventoryResponse;
import com.example.StockFlow.entity.Inventory;
import com.example.StockFlow.entity.Product;
import com.example.StockFlow.entity.Warehouse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    // === Création ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", source = "warehouse")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "inventoryMovements", ignore = true)
    Inventory toEntity(InventoryRequest request, Warehouse warehouse, Product product);

    // === Response ===
    @Mapping(target = "warehouseId", source = "warehouse.id")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productSku", source = "product.sku")
    @Mapping(target = "qtyOnHand", source = "qtyOnHand")
    @Mapping(target = "qtyReserved", source = "qtyReserved")
    InventoryResponse toResponse(Inventory inventory);

    // === Mise à jour particle ===
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", source = "warehouse")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "inventoryMovements", ignore = true)
    void updateEntityFromDto(InventoryRequest request, @MappingTarget Inventory inventory, Warehouse warehouse, Product product);
}
