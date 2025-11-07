package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.SupplierRequest;
import com.example.StockFlow.dto.response.SupplierResponse;
import com.example.StockFlow.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper
{
    SupplierResponse toResponse(Supplier supplier);
    Supplier toEntity(SupplierRequest supplierRequest);
    void updateEntityFromDTO(SupplierRequest supplierRequest, @MappingTarget Supplier supplier);
}
