package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.SalesOrderRequest;
import com.example.StockFlow.dto.response.SalesOrderResponse;
import com.example.StockFlow.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    // Créer une nouvelle commande
    @PostMapping
    public ResponseEntity<SalesOrderResponse> createSalesOrder(
            @RequestBody SalesOrderRequest request,
            @RequestHeader("Authorization") String token
    ) {
        SalesOrderResponse response = salesOrderService.createSalesOrder(request, token);
        return ResponseEntity.ok(response);
    }
}
