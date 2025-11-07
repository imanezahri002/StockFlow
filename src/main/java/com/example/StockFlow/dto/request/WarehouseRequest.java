package com.example.StockFlow.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class WarehouseRequest {
    private String name;
    private String location;
    private boolean active;
    private Long managerId; // juste l'ID du manager
}
