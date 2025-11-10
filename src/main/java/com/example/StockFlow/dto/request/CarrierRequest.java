package com.example.StockFlow.dto.request;

import com.example.StockFlow.entity.enums.CarrierStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class CarrierRequest {

    private Long id;

    private String code;
    private String name;
    private String email;
    private String contactPhone;
    private CarrierStatus status;

}
