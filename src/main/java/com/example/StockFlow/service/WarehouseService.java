package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.WarehouseRequest;
import com.example.StockFlow.dto.response.WarehouseResponse;
import com.example.StockFlow.entity.Manager;
import com.example.StockFlow.entity.Warehouse;
import com.example.StockFlow.exception.CustomException;
import com.example.StockFlow.mapper.WarehouseMapper;
import com.example.StockFlow.repository.ManagerRepository;
import com.example.StockFlow.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final ManagerRepository managerRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        Manager manager = managerRepository.findById(request.getManagerId())
                .orElseThrow(() -> new CustomException("Manager not found with ID: " + request.getManagerId()));

        Warehouse warehouse = warehouseMapper.toEntity(request, manager);
        return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(warehouseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new CustomException("Warehouse not found with ID: " + id));
        return warehouseMapper.toResponse(warehouse);
    }

    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new CustomException("Warehouse not found with ID: " + id));

        Manager manager = managerRepository.findById(request.getManagerId())
                .orElseThrow(() -> new CustomException("Manager not found with ID: " + request.getManagerId()));

        warehouseMapper.updateEntityFromDto(request, warehouse, manager);
        return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
    }

    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new CustomException("Warehouse not found with ID: " + id);
        }
        warehouseRepository.deleteById(id);
    }
}
