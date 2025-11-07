package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.InventoryRequest;
import com.example.StockFlow.dto.response.InventoryResponse;
import com.example.StockFlow.entity.Inventory;
import com.example.StockFlow.entity.Product;
import com.example.StockFlow.entity.Warehouse;
import com.example.StockFlow.exception.CustomException;
import com.example.StockFlow.mapper.InventoryMapper;
import com.example.StockFlow.repository.InventoryRepository;
import com.example.StockFlow.repository.ProductRepository;
import com.example.StockFlow.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryResponse createInventory(InventoryRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new CustomException("Warehouse not found with ID: " + request.getWarehouseId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException("Product not found with ID: " + request.getProductId()));

        Inventory inventory = inventoryMapper.toEntity(request, warehouse, product);
        return inventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public InventoryResponse getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new CustomException("Inventory not found with ID: " + id));
        return inventoryMapper.toResponse(inventory);
    }

    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new CustomException("Inventory not found with ID: " + id));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new CustomException("Warehouse not found with ID: " + request.getWarehouseId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException("Product not found with ID: " + request.getProductId()));

        inventoryMapper.updateEntityFromDto(request, inventory, warehouse, product);
        return inventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new CustomException("Inventory not found with ID: " + id);
        }
        inventoryRepository.deleteById(id);
    }
}
