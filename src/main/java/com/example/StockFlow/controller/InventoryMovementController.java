package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.InventoryMovementRequest;
import com.example.StockFlow.dto.response.InventoryMovementResponse;
import com.example.StockFlow.service.InventoryMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-movements")
@RequiredArgsConstructor
public class InventoryMovementController {

    private final InventoryMovementService service;

    // CREATE - WM only
    @PostMapping
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryMovementResponse> create(@Valid @RequestBody InventoryMovementRequest request) {
        return ResponseEntity.ok(service.createMovement(request));
    }

    // READ - ADMIN and WM
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<List<InventoryMovementResponse>> getAll() {
        return ResponseEntity.ok(service.getAllMovements());
    }

    // READ - ADMIN and WM
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryMovementResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMovementById(id));
    }

    // UPDATE - WM only (si nécessaire)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<InventoryMovementResponse> update(@PathVariable Long id, @Valid @RequestBody InventoryMovementRequest request) {
        // TODO: Implement update method in service
        return ResponseEntity.ok().build();
    }

    // DELETE - WM only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteMovement(id);
        return ResponseEntity.noContent().build();
    }
}
