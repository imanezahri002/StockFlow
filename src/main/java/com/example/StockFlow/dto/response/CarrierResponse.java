package com.example.StockFlow.dto.response;


import lombok.Data;

@Data
public class CarrierResponse {
    private Long id;

    private String code;
    private String name;
    private String email;
    private String contactPhone;
}
