package com.example.StockFlow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "SKU est obligatoire")
    private String sku;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    private String category;

    @NotNull(message = "Le statut actif est obligatoire")
    private Boolean active;

    private BigDecimal originalPrice;

    private BigDecimal profit;
}