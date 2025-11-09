package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.PurchaseOrderRequest;
import com.example.StockFlow.dto.response.PurchaseOrderResponse;
import com.example.StockFlow.entity.PurchaseOrder;
import com.example.StockFlow.entity.Supplier;
import com.example.StockFlow.entity.Manager;
import com.example.StockFlow.entity.enums.PurchaseOrderStatus;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = {PurchaseOrderLineMapper.class})
public interface PurchaseOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", expression = "java(supplier)")
    @Mapping(target = "manager", expression = "java(manager)")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target="warehouseId",ignore = true)
    @Mapping(target = "orderLines", ignore = true) // ajoutées après dans le service
    PurchaseOrder toEntity(PurchaseOrderRequest request, Supplier supplier, Manager manager);

    @Mapping(target = "supplierName", source = "supplier.companyName")
    @Mapping(target = "managerName", source = "manager.username")
    @Mapping(target="warehouseId" ,source="warehouseId")
    PurchaseOrderResponse toResponse(PurchaseOrder entity);
}
