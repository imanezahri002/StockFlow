// java
package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.InventoryMovementRequest;
import com.example.StockFlow.dto.response.InventoryMovementResponse;
import com.example.StockFlow.entity.Inventory;
import com.example.StockFlow.entity.InventoryMovement;
import com.example.StockFlow.entity.enums.MovementType;
import com.example.StockFlow.mapper.InventoryMovementMapper;
import com.example.StockFlow.repository.InventoryMovementRepository;
import com.example.StockFlow.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryMovementServiceTest {

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementMapper mapper;

    @InjectMocks
    private InventoryMovementService service;

    @Test
    void createMovement_success_returnsResponse() {
        InventoryMovementRequest req = new InventoryMovementRequest();
        req.setInventoryId(1L);
        Inventory inv = Inventory.builder().id(1L).build();

        InventoryMovement toSave = InventoryMovement.builder().inventory(inv).qty(5).build();
        InventoryMovement saved = InventoryMovement.builder().id(10L).inventory(inv).qty(5).build();
        InventoryMovementResponse resp = new InventoryMovementResponse();

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inv));
        when(mapper.toEntity(req, inv)).thenReturn(toSave);
        when(movementRepository.save(toSave)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(resp);

        InventoryMovementResponse result = service.createMovement(req);

        assertEquals(resp, result);
        verify(inventoryRepository).findById(1L);
        verify(mapper).toEntity(req, inv);
        verify(movementRepository).save(toSave);
        verify(mapper).toResponse(saved);
    }

    @Test
    void createMovement_inventoryNotFound_throwsRuntimeException() {
        InventoryMovementRequest req = new InventoryMovementRequest();
        req.setInventoryId(42L);

        when(inventoryRepository.findById(42L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createMovement(req));
        assertTrue(ex.getMessage().contains("Inventory not found"));
        verify(inventoryRepository).findById(42L);
        verifyNoInteractions(mapper, movementRepository);
    }

    @Test
    void getAllMovements_returnsMappedList() {
        InventoryMovement m1 = InventoryMovement.builder().id(1L).build();
        InventoryMovement m2 = InventoryMovement.builder().id(2L).build();
        InventoryMovementResponse r1 = new InventoryMovementResponse();
        InventoryMovementResponse r2 = new InventoryMovementResponse();

        when(movementRepository.findAll()).thenReturn(List.of(m1, m2));
        when(mapper.toResponse(m1)).thenReturn(r1);
        when(mapper.toResponse(m2)).thenReturn(r2);

        List<InventoryMovementResponse> list = service.getAllMovements();

        assertEquals(2, list.size());
        assertTrue(list.contains(r1));
        assertTrue(list.contains(r2));
        verify(movementRepository).findAll();
        verify(mapper).toResponse(m1);
        verify(mapper).toResponse(m2);
    }

    @Test
    void getMovementById_found_returnsResponse() {
        InventoryMovement m = InventoryMovement.builder().id(5L).build();
        InventoryMovementResponse resp = new InventoryMovementResponse();

        when(movementRepository.findById(5L)).thenReturn(Optional.of(m));
        when(mapper.toResponse(m)).thenReturn(resp);

        InventoryMovementResponse result = service.getMovementById(5L);

        assertEquals(resp, result);
        verify(movementRepository).findById(5L);
        verify(mapper).toResponse(m);
    }

    @Test
    void getMovementById_notFound_throwsRuntimeException() {
        when(movementRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getMovementById(99L));
        assertTrue(ex.getMessage().contains("Movement not found"));
        verify(movementRepository).findById(99L);
    }

    @Test
    void deleteMovement_exists_deletesSuccessfully() {
        when(movementRepository.existsById(7L)).thenReturn(true);
        doNothing().when(movementRepository).deleteById(7L);

        service.deleteMovement(7L);

        verify(movementRepository).existsById(7L);
        verify(movementRepository).deleteById(7L);
    }

    @Test
    void deleteMovement_notFound_throwsRuntimeException() {
        when(movementRepository.existsById(8L)).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteMovement(8L));
        assertTrue(ex.getMessage().contains("Movement not found"));
        verify(movementRepository).existsById(8L);
        verify(movementRepository, never()).deleteById(anyLong());
    }

    @Test
    void createOutboundMovement_savesOutboundMovement_withCorrectFields() {
        Inventory inv = Inventory.builder().id(3L).build();
        int qty = 4;
        String ref = "Order-1";
        String desc = "Sortie pour commande";

        ArgumentCaptor<InventoryMovement> captor = ArgumentCaptor.forClass(InventoryMovement.class);
        when(movementRepository.save(any(InventoryMovement.class))).thenAnswer(i -> i.getArgument(0));

        service.createOutboundMovement(inv, qty, ref, desc);

        verify(movementRepository).save(captor.capture());
        InventoryMovement saved = captor.getValue();

        assertEquals(inv, saved.getInventory());
        assertEquals(qty, saved.getQty());
        assertEquals(MovementType.OUTBOUND, saved.getType());
        assertEquals(ref, saved.getReferenceDocument());
        assertEquals(desc, saved.getDescription());
        assertNotNull(saved.getOccurredAt());
        // vérifier que la date est raisonnablement récente
        assertTrue(saved.getOccurredAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
