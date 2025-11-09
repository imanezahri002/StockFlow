package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.InventoryRequest;
import com.example.StockFlow.dto.response.InventoryResponse;
import com.example.StockFlow.entity.Inventory;
import com.example.StockFlow.entity.Product;
import com.example.StockFlow.entity.Warehouse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    // === Création d'une entité à partir d'un DTO ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", source = "warehouse")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "inventoryMovements", ignore = true)
    @Mapping(target = "name", source = "request.name") // si le DTO a un champ "name"
    Inventory toEntity(InventoryRequest request, Warehouse warehouse, Product product);

    // === Conversion d'une entité vers le DTO Response ===
    @Mapping(target = "warehouseId", source = "warehouse.id")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productSku", source = "product.sku")
    @Mapping(target = "qtyOnHand", source = "qtyOnHand")
    @Mapping(target = "qtyReserved", source = "qtyReserved")
    InventoryResponse toResponse(Inventory inventory);

    // === Mise à jour partielle de l'entité depuis le DTO ===
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", source = "warehouse")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "inventoryMovements", ignore = true)
    @Mapping(target = "name", source = "request.name") // si on veut mettre à jour le nom
    void updateEntityFromDto(InventoryRequest request, @MappingTarget Inventory inventory,
                             Warehouse warehouse, Product product);
}
