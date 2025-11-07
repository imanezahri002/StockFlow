package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.SupplierRequest;
import com.example.StockFlow.dto.response.SupplierResponse;
import com.example.StockFlow.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // Récupérer tous les fournisseurs
    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<SupplierResponse> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    // Récupérer un fournisseur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Créer un nouveau fournisseur
    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest supplierRequest) {
        SupplierResponse createdSupplier = supplierService.createSupplier(supplierRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }

    // Mettre à jour un fournisseur existant
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest supplierRequest) {
        SupplierResponse updatedSupplier = supplierService.updateSupplier(id, supplierRequest);
        return ResponseEntity.ok(updatedSupplier);
    }

    // Supprimer un fournisseur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
