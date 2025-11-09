package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.SalesOrderLineRequest;
import com.example.StockFlow.dto.response.SalesOrderLineResponse;
import com.example.StockFlow.entity.Product;
import com.example.StockFlow.entity.SalesOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalesOrderLineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "salesOrder", ignore = true)
    @Mapping(target = "product", expression = "java(product)")
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "backorder", ignore = true)
    SalesOrderLine toEntity(SalesOrderLineRequest request, Product product);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    SalesOrderLineResponse toResponse(SalesOrderLine entity);
}
