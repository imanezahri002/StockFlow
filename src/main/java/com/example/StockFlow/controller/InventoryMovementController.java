package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.InventoryMovementRequest;
import com.example.StockFlow.dto.response.InventoryMovementResponse;
import com.example.StockFlow.service.InventoryMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-movements")
@RequiredArgsConstructor
public class InventoryMovementController {

    private final InventoryMovementService service;

    @PostMapping
    public ResponseEntity<InventoryMovementResponse> create(@Valid @RequestBody InventoryMovementRequest request) {
        return ResponseEntity.ok(service.createMovement(request));
    }

    @GetMapping
    public ResponseEntity<List<InventoryMovementResponse>> getAll() {
        return ResponseEntity.ok(service.getAllMovements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryMovementResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMovementById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteMovement(id);
        return ResponseEntity.noContent().build();
    }
}
