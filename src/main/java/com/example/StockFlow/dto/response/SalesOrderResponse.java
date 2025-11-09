package com.example.StockFlow.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SalesOrderResponse {
    private Long id;
    private Long userId;
    private String status;
    private LocalDateTime createdAt;
    private List<SalesOrderLineResponse> orderLines;
}
