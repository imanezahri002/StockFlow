package com.example.StockFlow.entity;


import com.example.StockFlow.entity.enums.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne

    private Supplier supplier;
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;

    private Long warehouseId;
    @ManyToOne
    private Manager manager;

    private LocalDateTime createdAt;
    private LocalDateTime expectedDelivery;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<PurchaseOrderLine> orderLines;


}

