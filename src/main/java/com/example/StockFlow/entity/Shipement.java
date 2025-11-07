package com.example.StockFlow.entity;

import com.example.StockFlow.entity.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private LocalDateTime plannedDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;

    @ManyToOne
    private Carrier carrier;

    @ManyToOne
    private SalesOrder salesOrder;
}


