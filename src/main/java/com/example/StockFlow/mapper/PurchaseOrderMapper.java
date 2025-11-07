package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.PurchaseOrderLineRequest;
import com.example.StockFlow.dto.request.PurchaseOrderRequest;
import com.example.StockFlow.dto.response.PurchaseOrderLineResponse;
import com.example.StockFlow.dto.response.PurchaseOrderResponse;
import com.example.StockFlow.entity.PurchaseOrder;
import com.example.StockFlow.entity.PurchaseOrderLine;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PurchaseOrderMapper {

    // -------------------------------
    // Entity -> Response
    // -------------------------------
    @Mapping(target = "supplierName", source = "supplier.companyName")
    @Mapping(target = "managerName", source = "manager.id")
    @Mapping(target = "orderLines", source = "orderLines")
    PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder);

    List<PurchaseOrderResponse> toResponseList(List<PurchaseOrder> entities);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "totalPrice", expression = "java(line.getUnitPrice() * line.getQuantity())")
    PurchaseOrderLineResponse toLineResponse(PurchaseOrderLine line);

    // -------------------------------
    // Request DTO -> Entity
    // -------------------------------
    @Mapping(target = "id", ignore = true) // Pour éviter d’écraser l'id
    @Mapping(target = "manager", ignore = true) // On l'assigne après via le manager connecté
    @Mapping(target = "status", ignore = true)  // On peut définir PENDING par défaut dans le service
    @Mapping(target = "createdAt", ignore = true) // Défini dans le service
    @Mapping(target = "orderLines", source = "orderLine") // Mapping des lignes
    PurchaseOrder toEntity(PurchaseOrderRequest request);

    List<PurchaseOrder> toEntityList(List<PurchaseOrderRequest> requests);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true) // À récupérer via ProductRepository
    @Mapping(target = "purchaseOrder", ignore = true) // Assigné dans le service
    PurchaseOrderLine toLineEntity(PurchaseOrderLineRequest lineRequest);
}
