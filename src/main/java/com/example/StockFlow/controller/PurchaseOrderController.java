package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.PurchaseOrderRequest;
import com.example.StockFlow.dto.response.PurchaseOrderResponse;
import com.example.StockFlow.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    // CREATE - ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PurchaseOrderResponse> createOrder(
            @RequestBody PurchaseOrderRequest request,
            @RequestHeader("Authorization") String token
    ) {
        // Appel du service
        PurchaseOrderResponse response = purchaseOrderService.createOrder(request, token);
        return ResponseEntity.ok(response);
    }

    // READ - ADMIN and WM
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<?> getAllOrders() {
        // TODO: Implement in service
        return ResponseEntity.ok().build();
    }

    // READ - ADMIN and WM
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        // TODO: Implement in service
        return ResponseEntity.ok().build();
    }

    // UPDATE - ADMIN only (approve)
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PurchaseOrderResponse> approveOrder(
            @PathVariable Long id
    ) {
        PurchaseOrderResponse response = purchaseOrderService.approvePurchaseOrder(id);
        return ResponseEntity.ok(response);
    }

    // DELETE - ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        // TODO: Implement in service
        return ResponseEntity.noContent().build();
    }
}
