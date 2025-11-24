package com.example.StockFlow.dto.response;

import com.example.StockFlow.dto.response.CarrierResponse;
import com.example.StockFlow.dto.response.SalesOrderResponse;
import com.example.StockFlow.entity.enums.ShipmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShippementResponse {

    private Long id;

    private String trackingNumber;

    private ShipmentStatus status;

    private LocalDateTime plannedDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;

    private CarrierResponse carrier;

    private SalesOrderResponse salesOrder;
}
