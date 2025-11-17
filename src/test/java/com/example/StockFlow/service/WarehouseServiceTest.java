// java
package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.WarehouseRequest;
import com.example.StockFlow.dto.response.WarehouseResponse;
import com.example.StockFlow.entity.Manager;
import com.example.StockFlow.entity.Warehouse;
import com.example.StockFlow.exception.CustomException;
import com.example.StockFlow.mapper.WarehouseMapper;
import com.example.StockFlow.repository.ManagerRepository;
import com.example.StockFlow.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseService warehouseService;

    @Test
    void createWarehouse_managerNotFound_throwsCustomException() {
        WarehouseRequest req = mock(WarehouseRequest.class);
        when(req.getManagerId()).thenReturn(1L);
        when(managerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> warehouseService.createWarehouse(req));
        verify(managerRepository).findById(1L);
        verifyNoInteractions(warehouseRepository);
    }

    @Test
    void createWarehouse_success_returnsResponse() {
        WarehouseRequest req = mock(WarehouseRequest.class);
        when(req.getManagerId()).thenReturn(2L);

        Manager manager = new Manager();
        Warehouse toSave = new Warehouse();
        Warehouse saved = new Warehouse();
        WarehouseResponse resp = new WarehouseResponse();

        when(managerRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(warehouseMapper.toEntity(req, manager)).thenReturn(toSave);
        when(warehouseRepository.save(toSave)).thenReturn(saved);
        when(warehouseMapper.toResponse(saved)).thenReturn(resp);

        WarehouseResponse result = warehouseService.createWarehouse(req);

        assertEquals(resp, result);
        verify(managerRepository).findById(2L);
        verify(warehouseMapper).toEntity(req, manager);
        verify(warehouseRepository).save(toSave);
        verify(warehouseMapper).toResponse(saved);
    }

    @Test
    void getAllWarehouses_returnsMappedList() {
        Warehouse w1 = new Warehouse();
        Warehouse w2 = new Warehouse();
        WarehouseResponse r1 = new WarehouseResponse();
        WarehouseResponse r2 = new WarehouseResponse();

        when(warehouseRepository.findAll()).thenReturn(List.of(w1, w2));
        when(warehouseMapper.toResponse(w1)).thenReturn(r1);
        when(warehouseMapper.toResponse(w2)).thenReturn(r2);

        List<WarehouseResponse> list = warehouseService.getAllWarehouses();

        assertEquals(2, list.size());
        assertTrue(list.contains(r1));
        assertTrue(list.contains(r2));
        verify(warehouseRepository).findAll();
    }

    @Test
    void getWarehouseById_notFound_throwsCustomException() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> warehouseService.getWarehouseById(99L));
        verify(warehouseRepository).findById(99L);
    }

    @Test
    void updateWarehouse_managerNotFound_throwsCustomException() {
        WarehouseRequest req = mock(WarehouseRequest.class);
        when(req.getManagerId()).thenReturn(5L);

        Warehouse existing = new Warehouse();
        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(managerRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> warehouseService.updateWarehouse(10L, req));
        verify(warehouseRepository).findById(10L);
        verify(managerRepository).findById(5L);
    }

    @Test
    void deleteWarehouse_notFound_throwsCustomException() {
        when(warehouseRepository.existsById(7L)).thenReturn(false);
        assertThrows(CustomException.class, () -> warehouseService.deleteWarehouse(7L));
        verify(warehouseRepository).existsById(7L);
        verify(warehouseRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteWarehouse_exists_deletesSuccessfully() {
        when(warehouseRepository.existsById(8L)).thenReturn(true);
        doNothing().when(warehouseRepository).deleteById(8L);

        warehouseService.deleteWarehouse(8L);

        verify(warehouseRepository).existsById(8L);
        verify(warehouseRepository).deleteById(8L);
    }
}
