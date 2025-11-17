package com.example.StockFlow.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SalesOrderRequest {
    private boolean backorder;
    private List<SalesOrderLineRequest>  orderLines;
}
