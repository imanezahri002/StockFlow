package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.PurchaseOrderRequest;
import com.example.StockFlow.dto.response.PurchaseOrderResponse;
import com.example.StockFlow.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createOrder(
            @RequestBody PurchaseOrderRequest request,
            @RequestHeader("Authorization") String token
    ) {
        // Appel du service
        PurchaseOrderResponse response = purchaseOrderService.createOrder(request, token);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/approve")
    public ResponseEntity<PurchaseOrderResponse> approveOrder(
            @PathVariable Long id
    ) {
        PurchaseOrderResponse response = purchaseOrderService.approvePurchaseOrder(id);
        return ResponseEntity.ok(response);
    }
}
