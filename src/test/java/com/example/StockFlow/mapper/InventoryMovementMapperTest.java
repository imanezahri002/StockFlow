// java
package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.InventoryMovementRequest;
import com.example.StockFlow.dto.response.InventoryMovementResponse;
import com.example.StockFlow.entity.Inventory;
import com.example.StockFlow.entity.InventoryMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMovementMapperTest {

    private InventoryMovementMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(InventoryMovementMapper.class);
    }

    @Test
    void toEntity_withProvidedOccurredAt_setsProvidedDate_and_keepsInventory() {
        InventoryMovementRequest req = new InventoryMovementRequest();
        LocalDateTime provided = LocalDateTime.of(2024, 12, 31, 10, 30, 0);
        req.setOccurredAt(provided);

        Inventory inventory = new Inventory();
        inventory.setId(10L);
        inventory.setName("Main Inv");

        InventoryMovement entity = mapper.toEntity(req, inventory);

        assertNotNull(entity);
        assertNull(entity.getId(), "id doit être ignoré par le mapping");
        assertSame(inventory, entity.getInventory());
        assertEquals(provided, entity.getOccurredAt());
    }

    @Test
    void toEntity_whenOccurredAtNull_setsNow_and_keepsInventory() {
        InventoryMovementRequest req = new InventoryMovementRequest();
        req.setOccurredAt(null);

        Inventory inventory = new Inventory();
        inventory.setId(11L);
        inventory.setName("Secondary");

        LocalDateTime before = LocalDateTime.now();
        InventoryMovement entity = mapper.toEntity(req, inventory);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(entity.getOccurredAt());
        // occurredAt doit être entre before et after (inclus)
        assertFalse(entity.getOccurredAt().isBefore(before), "occurredAt ne doit pas être avant le timestamp capturé avant le mapping");
        assertFalse(entity.getOccurredAt().isAfter(after), "occurredAt ne doit pas être après le timestamp capturé après le mapping");
        assertSame(inventory, entity.getInventory());
    }

    @Test
    void toResponse_mapsInventoryIdAndName() {
        InventoryMovement entity = new InventoryMovement();
        Inventory inventory = new Inventory();
        inventory.setId(20L);
        inventory.setName("Depot X");
        entity.setInventory(inventory);

        InventoryMovementResponse resp = mapper.toResponse(entity);

        assertNotNull(resp);
        assertEquals(inventory.getId(), resp.getInventoryId());
        assertEquals(inventory.getName(), resp.getInventoryName());
    }
}
