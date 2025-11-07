package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.WarehouseRequest;
import com.example.StockFlow.dto.response.WarehouseResponse;
import com.example.StockFlow.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseResponse> create(@RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.createWarehouse(request));
    }

    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAll() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> update(@PathVariable Long id, @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok("Warehouse deleted successfully");
    }
}
