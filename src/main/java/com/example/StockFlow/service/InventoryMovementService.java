package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.InventoryMovementRequest;
import com.example.StockFlow.dto.response.InventoryMovementResponse;
import com.example.StockFlow.entity.Inventory;
import com.example.StockFlow.entity.InventoryMovement;
import com.example.StockFlow.mapper.InventoryMovementMapper;
import com.example.StockFlow.repository.InventoryMovementRepository;
import com.example.StockFlow.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryMovementService {

    private final InventoryMovementRepository movementRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementMapper mapper;

    public InventoryMovementResponse createMovement(InventoryMovementRequest request) {
        Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new RuntimeException("Inventory not found with ID: " + request.getInventoryId()));

        InventoryMovement movement = mapper.toEntity(request, inventory);
        InventoryMovement saved = movementRepository.save(movement);
        return mapper.toResponse(saved);
    }

    public List<InventoryMovementResponse> getAllMovements() {
        return movementRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public InventoryMovementResponse getMovementById(Long id) {
        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movement not found with ID: " + id));
        return mapper.toResponse(movement);
    }

    public void deleteMovement(Long id) {
        if (!movementRepository.existsById(id)) {
            throw new RuntimeException("Movement not found with ID: " + id);
        }
        movementRepository.deleteById(id);
    }
}
