package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.ShippementRequest;
import com.example.StockFlow.dto.response.ShippementResponse;
import com.example.StockFlow.entity.Shippement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShippementMapper {

    @Mapping(source = "carrier.id", target = "carrier.id")
    @Mapping(source = "carrier.name", target = "carrier.name")
    @Mapping(source = "salesOrder.id", target = "salesOrder.id")
    ShippementResponse toResponse(Shippement shipment);

    @Mapping(target = "carrier", ignore = true) // à gérer dans le service
    @Mapping(target = "salesOrder", ignore = true) // à gérer dans le service
    Shippement toEntity(ShippementRequest request);


    // --- Update existing entity ---
    @Mapping(target = "id", ignore = true) // on ne change pas l'ID
    @Mapping(target = "carrier", ignore = true) // gérer dans le service
    @Mapping(target = "salesOrder", ignore = true) // gérer dans le service
    void updateEntityFromRequest(ShippementRequest request, @MappingTarget Shippement shipment);

}
