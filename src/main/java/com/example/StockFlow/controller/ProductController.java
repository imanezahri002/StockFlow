package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.ProductRequest;
import com.example.StockFlow.dto.response.ProductResponse;
import com.example.StockFlow.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ProductController {

    private final ProductService productService;

    // CREATE - ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ - Get by ID (ADMIN, WM, CLIENT - mais CLIENT voit uniquement actifs)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        log.info("Example info message -> Received product ID {}", id);
        log.debug("Example debug message -> Received product ID {}", id);
        log.error("Example error message -> not found product ID {}", id);
        return ResponseEntity.ok(response);
    }

    // READ - Get by SKU (ADMIN, WM, CLIENT)
    @GetMapping("/sku/{sku}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        ProductResponse response = productService.getProductBySku(sku);
        return ResponseEntity.ok(response);
    }

    // READ - Get All (ADMIN, WM, CLIENT)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search) {

        List<ProductResponse> responses;

        if (category != null) {
            responses = productService.getProductsByCategory(category);
        } else if (active != null) {
            responses = productService.getProductsByActiveStatus(active);
        } else if (search != null) {
            responses = productService.searchProductsByName(search);
        } else {
            responses = productService.getAllProducts();
        }

        return ResponseEntity.ok(responses);
    }

    // UPDATE - ADMIN only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE - ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Soft Delete - Désactiver - ADMIN only
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> deactivateProduct(@PathVariable Long id) {
        ProductResponse response = productService.deactivateProduct(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{sku}/blocProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> desactiverProductBySku(@PathVariable @Valid String sku){
        System.out.println("Desactivation du produit avec le SKU: " + sku);
        ProductResponse response=productService.desactiverProductBySku(sku);
        return ResponseEntity.ok(response);
    }

    // Réactiver - ADMIN only
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> activateProduct(@PathVariable Long id) {
        ProductResponse response = productService.activateProduct(id);
        return ResponseEntity.ok(response);
    }
}