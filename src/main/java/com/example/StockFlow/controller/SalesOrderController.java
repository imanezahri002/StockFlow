package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.SalesOrderRequest;
import com.example.StockFlow.dto.response.SalesOrderResponse;
import com.example.StockFlow.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    // CREATE - CLIENT only (ses propres commandes)
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<SalesOrderResponse> createSalesOrder(
            @RequestBody SalesOrderRequest request
    ){
        SalesOrderResponse response = salesOrderService.createSalesOrder(request);
        return ResponseEntity.ok(response);
    }

    // READ - CLIENT (ses commandes), ADMIN (toutes), WM (pour réservation/shipping)
    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<List<SalesOrderResponse>> getAllOrders() {
        // TODO: Filter by user role - CLIENT sees only their orders
        return ResponseEntity.ok().build();
    }

    // READ - CLIENT (sa commande), ADMIN, WM
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<SalesOrderResponse> getOrderById(@PathVariable Long id) {
        // TODO: Verify ownership for CLIENT
        return ResponseEntity.ok().build();
    }

    // UPDATE - CLIENT (sa commande si status CREATED)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<SalesOrderResponse> updateOrder(
            @PathVariable Long id,
            @RequestBody SalesOrderRequest request
    ) {
        // TODO: Verify ownership and status
        return ResponseEntity.ok().build();
    }

    // UPDATE - ADMIN or WM (réservation)
    @PutMapping("/{id}/reserve")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<SalesOrderResponse> reserveOrder(@PathVariable Long id) {
        // TODO: Implement reservation logic
        return ResponseEntity.ok().build();
    }

    // UPDATE - ADMIN or WM (shipping)
    @PutMapping("/{id}/ship")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<SalesOrderResponse> shipOrder(@PathVariable Long id) {
        // TODO: Implement shipping logic
        return ResponseEntity.ok().build();
    }

    // DELETE - CLIENT (sa commande si status CREATED)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        // TODO: Verify ownership and status
        return ResponseEntity.noContent().build();
    }
}
