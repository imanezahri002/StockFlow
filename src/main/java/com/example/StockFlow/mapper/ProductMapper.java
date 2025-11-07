package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.ProductRequest;

import com.example.StockFlow.dto.response.ProductResponse;
import com.example.StockFlow.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    Product toEntity(ProductRequest request);

    void updateEntityFromDTO(ProductRequest request, @MappingTarget Product product);
}