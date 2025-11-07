package com.example.StockFlow.dto.response;

import com.example.StockFlow.entity.enums.PurchaseOrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderResponse {
    private Long id;
    private String supplierName;
    private String managerName;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private List<PurchaseOrderLineResponse> orderLines;
}
