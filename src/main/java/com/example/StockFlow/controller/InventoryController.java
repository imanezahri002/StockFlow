package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.InventoryRequest;
import com.example.StockFlow.dto.response.InventoryResponse;
import com.example.StockFlow.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.createInventory(request));
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAll() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> update(@PathVariable Long id, @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
