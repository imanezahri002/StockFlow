package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.SupplierRequest;
import com.example.StockFlow.dto.response.SupplierResponse;
import com.example.StockFlow.entity.Supplier;
import com.example.StockFlow.mapper.SupplierMapper;
import com.example.StockFlow.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    public Optional<SupplierResponse> getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(supplierMapper::toResponse);
    }

    @Transactional
    public SupplierResponse createSupplier(SupplierRequest supplierRequest) {
        Supplier supplier = supplierMapper.toEntity(supplierRequest);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(savedSupplier);
    }

    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest supplierRequest) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Fournisseur introuvable");
        }
        Supplier supplier = supplierMapper.toEntity(supplierRequest);
        supplier.setId(id);
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(updatedSupplier);
    }

    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Fournisseur introuvable");
        }
        supplierRepository.deleteById(id);
    }
}
