package com.example.StockFlow.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SalesOrderRequest {
    private List<SalesOrderLineRequest>  orderLines;
}
