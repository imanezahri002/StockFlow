package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.RegisterRequest;
import com.example.StockFlow.dto.response.UserResponse;
import com.example.StockFlow.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")

public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    User toEntity(RegisterRequest request);


    UserResponse toResponse(User user);
}
