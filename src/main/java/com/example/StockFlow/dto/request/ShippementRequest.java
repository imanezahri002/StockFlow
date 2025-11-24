package com.example.StockFlow.dto.request;

import com.example.StockFlow.entity.enums.ShipmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShippementRequest {

    private String trackingNumber;

    private ShipmentStatus status;

    private LocalDateTime plannedDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;

    private Long carrierId;

    private Long salesOrderId;
}
