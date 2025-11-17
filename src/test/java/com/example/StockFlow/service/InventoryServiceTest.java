
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void createInventory_success_returnsResponse() {
        InventoryRequest req = new InventoryRequest();
        req.setWarehouseId(1L);
        req.setProductId(2L);
        Inventory invToSave = new Inventory();
        Inventory saved = new Inventory();
        InventoryResponse resp = new InventoryResponse();
        Warehouse wh = new Warehouse();
        Product pr = new Product();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(wh));
        when(productRepository.findById(2L)).thenReturn(Optional.of(pr));
        when(inventoryMapper.toEntity(req, wh, pr)).thenReturn(invToSave);
        when(inventoryRepository.save(invToSave)).thenReturn(saved);
        when(inventoryMapper.toResponse(saved)).thenReturn(resp);

        InventoryResponse result = inventoryService.createInventory(req);

        assertEquals(resp, result);
        verify(warehouseRepository).findById(1L);
        verify(productRepository).findById(2L);
        verify(inventoryMapper).toEntity(req, wh, pr);
        verify(inventoryRepository).save(invToSave);
        verify(inventoryMapper).toResponse(saved);
    }

    @Test
    void createInventory_warehouseNotFound_throwsCustomException() {
        InventoryRequest req = new InventoryRequest();
        req.setWarehouseId(10L);
        when(warehouseRepository.findById(10L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> inventoryService.createInventory(req));
        assertTrue(ex.getMessage().contains("Warehouse not found"));
        verify(warehouseRepository).findById(10L);
        verifyNoInteractions(productRepository, inventoryMapper, inventoryRepository);
    }

    @Test
    void createInventory_productNotFound_throwsCustomException() {
        InventoryRequest req = new InventoryRequest();
        req.setWarehouseId(1L);
        req.setProductId(99L);
        Warehouse wh = new Warehouse();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(wh));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> inventoryService.createInventory(req));
        assertTrue(ex.getMessage().contains("Product not found"));
        verify(warehouseRepository).findById(1L);
        verify(productRepository).findById(99L);
        verifyNoInteractions(inventoryMapper, inventoryRepository);
    }

    @Test
    void getAllInventories_returnsMappedList() {
        Inventory i1 = new Inventory();
        Inventory i2 = new Inventory();
        InventoryResponse r1 = new InventoryResponse();
        InventoryResponse r2 = new InventoryResponse();

        when(inventoryRepository.findAll()).thenReturn(List.of(i1, i2));
        when(inventoryMapper.toResponse(i1)).thenReturn(r1);
        when(inventoryMapper.toResponse(i2)).thenReturn(r2);

        List<InventoryResponse> list = inventoryService.getAllInventories();

        assertEquals(2, list.size());
        assertTrue(list.contains(r1));
        assertTrue(list.contains(r2));
        verify(inventoryRepository).findAll();
    }

    @Test
    void getInventoryById_found_returnsResponse() {
        Inventory inv = new Inventory();
        InventoryResponse resp = new InventoryResponse();

        when(inventoryRepository.findById(5L)).thenReturn(Optional.of(inv));
        when(inventoryMapper.toResponse(inv)).thenReturn(resp);

        InventoryResponse result = inventoryService.getInventoryById(5L);

        assertEquals(resp, result);
        verify(inventoryRepository).findById(5L);
        verify(inventoryMapper).toResponse(inv);
    }

    @Test
    void getInventoryById_notFound_throwsCustomException() {
        when(inventoryRepository.findById(7L)).thenReturn(Optional.empty());
        CustomException ex = assertThrows(CustomException.class, () -> inventoryService.getInventoryById(7L));
        assertTrue(ex.getMessage().contains("Inventory not found"));
        verify(inventoryRepository).findById(7L);
    }

    @Test
    void updateInventory_success_updatesRelationsAndCallsMapperAndSaves() {
        Long id = 11L;
        Inventory existing = new Inventory();
        InventoryRequest req = new InventoryRequest();
        req.setWarehouseId(3L);
        req.setProductId(4L);

        Warehouse newWh = new Warehouse();
        Product newPr = new Product();
        Inventory saved = new Inventory();
        InventoryResponse resp = new InventoryResponse();

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(warehouseRepository.findById(3L)).thenReturn(Optional.of(newWh));
        when(productRepository.findById(4L)).thenReturn(Optional.of(newPr));
        // updateEntityFromDto is void — we just verify it's called
        doNothing().when(inventoryMapper).updateEntityFromDto(req, existing);
        when(inventoryRepository.save(existing)).thenReturn(saved);
        when(inventoryMapper.toResponse(saved)).thenReturn(resp);

        InventoryResponse result = inventoryService.updateInventory(id, req);

        assertEquals(resp, result);
        verify(inventoryRepository).findById(id);
        verify(warehouseRepository).findById(3L);
        verify(productRepository).findById(4L);
        verify(inventoryMapper).updateEntityFromDto(req, existing);
        verify(inventoryRepository).save(existing);
        verify(inventoryMapper).toResponse(saved);
    }

    @Test
    void updateInventory_notFound_throwsCustomException() {
        when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());
        InventoryRequest req = new InventoryRequest();
        CustomException ex = assertThrows(CustomException.class, () -> inventoryService.updateInventory(99L, req));
        assertTrue(ex.getMessage().contains("Inventory not found"));
        verify(inventoryRepository).findById(99L);
        verifyNoMoreInteractions(warehouseRepository, productRepository, inventoryMapper);
    }

    @Test
    void deleteInventory_exists_deletesSuccessfully() {
        when(inventoryRepository.existsById(20L)).thenReturn(true);
        doNothing().when(inventoryRepository).deleteById(20L);

        inventoryService.deleteInventory(20L);

        verify(inventoryRepository).existsById(20L);
        verify(inventoryRepository).deleteById(20L);
    }

    @Test
    void deleteInventory_notFound_throwsCustomException() {
        when(inventoryRepository.existsById(21L)).thenReturn(false);
        CustomException ex = assertThrows(CustomException.class, () -> inventoryService.deleteInventory(21L));
        assertTrue(ex.getMessage().contains("Inventory not found"));
        verify(inventoryRepository).existsById(21L);
        verify(inventoryRepository, never()).deleteById(anyLong());
    }
}
