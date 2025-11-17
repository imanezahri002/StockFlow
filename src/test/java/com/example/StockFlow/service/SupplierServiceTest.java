// java
package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.SupplierRequest;
import com.example.StockFlow.dto.response.SupplierResponse;
import com.example.StockFlow.entity.Supplier;
import com.example.StockFlow.mapper.SupplierMapper;
import com.example.StockFlow.repository.SupplierRepository;
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
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    @Test
    void getAllSuppliers_returnsMappedList() {
        Supplier s1 = new Supplier();
        Supplier s2 = new Supplier();
        SupplierResponse r1 = new SupplierResponse();
        SupplierResponse r2 = new SupplierResponse();

        when(supplierRepository.findAll()).thenReturn(List.of(s1, s2));
        when(supplierMapper.toResponse(s1)).thenReturn(r1);
        when(supplierMapper.toResponse(s2)).thenReturn(r2);

        List<SupplierResponse> result = supplierService.getAllSuppliers();

        assertEquals(2, result.size());
        assertTrue(result.contains(r1));
        assertTrue(result.contains(r2));
        verify(supplierRepository).findAll();

        // Capturer les appels au mapper et vérifier qu'on a bien mappé les deux instances
        ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
        verify(supplierMapper, times(2)).toResponse(captor.capture());
        List<Supplier> mappedSuppliers = captor.getAllValues();
        assertTrue(mappedSuppliers.contains(s1));
        assertTrue(mappedSuppliers.contains(s2));
    }

    @Test
    void getSupplierById_found_returnsOptionalResponse() {
        Supplier s = new Supplier();
        SupplierResponse resp = new SupplierResponse();
        when(supplierRepository.findById(5L)).thenReturn(Optional.of(s));
        when(supplierMapper.toResponse(s)).thenReturn(resp);

        Optional<SupplierResponse> out = supplierService.getSupplierById(5L);

        assertTrue(out.isPresent());
        assertEquals(resp, out.get());
        verify(supplierRepository).findById(5L);
        verify(supplierMapper).toResponse(s);
    }

    @Test
    void getSupplierById_notFound_returnsEmptyOptional() {
        when(supplierRepository.findById(9L)).thenReturn(Optional.empty());

        Optional<SupplierResponse> out = supplierService.getSupplierById(9L);

        assertTrue(out.isEmpty());
        verify(supplierRepository).findById(9L);
        verifyNoInteractions(supplierMapper);
    }

    @Test
    void createSupplier_success_returnsResponse() {
        SupplierRequest req = mock(SupplierRequest.class);
        Supplier toSave = new Supplier();
        Supplier saved = new Supplier();
        saved.setId(10L);
        SupplierResponse resp = new SupplierResponse();

        when(supplierMapper.toEntity(req)).thenReturn(toSave);
        when(supplierRepository.save(toSave)).thenReturn(saved);
        when(supplierMapper.toResponse(saved)).thenReturn(resp);

        SupplierResponse out = supplierService.createSupplier(req);

        assertEquals(resp, out);
        verify(supplierMapper).toEntity(req);
        verify(supplierRepository).save(toSave);
        verify(supplierMapper).toResponse(saved);
    }

    @Test
    void updateSupplier_success_setsIdAndReturnsResponse() {
        Long id = 15L;
        SupplierRequest req = mock(SupplierRequest.class);
        Supplier entity = new Supplier();
        SupplierResponse resp = new SupplierResponse();

        when(supplierRepository.existsById(id)).thenReturn(true);
        when(supplierMapper.toEntity(req)).thenReturn(entity);
        when(supplierRepository.save(entity)).thenReturn(entity);
        when(supplierMapper.toResponse(entity)).thenReturn(resp);

        SupplierResponse out = supplierService.updateSupplier(id, req);

        assertEquals(resp, out);
        assertEquals(id, entity.getId());
        verify(supplierRepository).existsById(id);
        verify(supplierMapper).toEntity(req);
        verify(supplierRepository).save(entity);
        verify(supplierMapper).toResponse(entity);
    }

    @Test
    void updateSupplier_notFound_throwsRuntimeException() {
        Long id = 20L;
        SupplierRequest req = mock(SupplierRequest.class);
        when(supplierRepository.existsById(id)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> supplierService.updateSupplier(id, req));
        assertTrue(ex.getMessage().contains("Fournisseur introuvable"));
        verify(supplierRepository).existsById(id);
        verifyNoMoreInteractions(supplierMapper, supplierRepository);
    }

    @Test
    void deleteSupplier_exists_deletesSuccessfully() {
        Long id = 30L;
        when(supplierRepository.existsById(id)).thenReturn(true);
        doNothing().when(supplierRepository).deleteById(id);

        supplierService.deleteSupplier(id);

        verify(supplierRepository).existsById(id);
        verify(supplierRepository).deleteById(id);
    }

    @Test
    void deleteSupplier_notFound_throwsRuntimeException() {
        Long id = 40L;
        when(supplierRepository.existsById(id)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> supplierService.deleteSupplier(id));
        assertTrue(ex.getMessage().contains("Fournisseur introuvable"));
        verify(supplierRepository).existsById(id);
        verify(supplierRepository, never()).deleteById(anyLong());
    }
}
