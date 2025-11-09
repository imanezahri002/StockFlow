package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.SalesOrderRequest;
import com.example.StockFlow.dto.response.SalesOrderResponse;
import com.example.StockFlow.entity.SalesOrder;
import com.example.StockFlow.entity.User;
import com.example.StockFlow.entity.enums.OrderStatus;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = {SalesOrderLineMapper.class})
public interface SalesOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", expression = "java(user)")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orderLines", ignore = true) // sera ajoutée dans le service après
    SalesOrder toEntity(SalesOrderRequest request, User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "status", source = "status")
    SalesOrderResponse toResponse(SalesOrder entity);
}
