package com.example.StockFlow.mapper;


import com.example.StockFlow.dto.request.WarehouseRequest;
import com.example.StockFlow.dto.response.WarehouseResponse;
import com.example.StockFlow.entity.Manager;
import com.example.StockFlow.entity.Warehouse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    // DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", source = "manager")
    @Mapping(target = "active", ignore = true)
    Warehouse toEntity(WarehouseRequest request, Manager manager);

    // Entity → DTO
    @Mapping(target = "managerName", source = "manager.username")
    @Mapping(target = "managerEmail", source = "manager.email")
    WarehouseResponse toResponse(Warehouse warehouse);

    // Update Entity from DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "manager", source = "manager")
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDto(WarehouseRequest request, @MappingTarget Warehouse warehouse, Manager manager);
}

