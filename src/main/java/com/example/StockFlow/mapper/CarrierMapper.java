package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.CarrierRequest;
import com.example.StockFlow.dto.response.CarrierResponse;
import com.example.StockFlow.entity.Carrier;
import org.mapstruct.*;


@Mapper(componentModel = "spring")

public interface CarrierMapper {

    Carrier toEntity(CarrierRequest request);
    CarrierResponse toResponse(Carrier carrier);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CarrierRequest request, @MappingTarget Carrier carrier);

}
