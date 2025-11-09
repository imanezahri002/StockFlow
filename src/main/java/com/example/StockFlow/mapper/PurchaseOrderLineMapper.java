package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.PurchaseOrderLineRequest;
import com.example.StockFlow.dto.response.PurchaseOrderLineResponse;
import com.example.StockFlow.entity.PurchaseOrderLine;
import com.example.StockFlow.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseOrderLineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "product", expression = "java(product)")
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PurchaseOrderLine toEntity(PurchaseOrderLineRequest request, Product product);

    @Mapping(target = "productName", source = "product.name")
    PurchaseOrderLineResponse toResponse(PurchaseOrderLine entity);
}
