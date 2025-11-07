package com.example.StockFlow.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseOrderRequest {
    private Long supplierId;
    private List<PurchaseOrderLineRequest> orderLine;
}
