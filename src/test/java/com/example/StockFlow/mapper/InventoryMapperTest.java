// java
package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.InventoryRequest;
import com.example.StockFlow.dto.response.InventoryResponse;
import com.example.StockFlow.entity.Inventory;
import com.example.StockFlow.entity.Product;
import com.example.StockFlow.entity.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMapperTest {

    private InventoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(InventoryMapper.class);
    }

    @Test
    void toEntity_ignoresId_setsRelations_and_mapsName() {
        InventoryRequest req = new InventoryRequest();
        req.setName("Stock A");

        Warehouse warehouse = new Warehouse();
        warehouse.setId(7L);
        warehouse.setName("Entrepot A");

        Product product = new Product();
        product.setId(15L);
        product.setName("Produit X");
        product.setSku("SKU-X");

        Inventory entity = mapper.toEntity(req, warehouse, product);

        assertNotNull(entity);
        assertNull(entity.getId(), "L'id doit être ignoré lors de la création");
        assertSame(warehouse, entity.getWarehouse(), "Le warehouse fourni doit être référencé");
        assertSame(product, entity.getProduct(), "Le product fourni doit être référencé");
        assertEquals("Stock A", entity.getName(), "Le champ name doit être mappé depuis request.name");
    }

    @Test
    void toResponse_mapsWarehouseAndProductAndQuantities() {
        Inventory inventory = new Inventory();
        inventory.setId(3L);

        Warehouse wh = new Warehouse();
        wh.setId(21L);
        wh.setName("Depot Z");
        inventory.setWarehouse(wh);

        Product pr = new Product();
        pr.setId(42L);
        pr.setName("Produit Y");
        pr.setSku("SKU-Y");
        inventory.setProduct(pr);

        inventory.setQtyOnHand(100);
        inventory.setQtyReserved(12);

        InventoryResponse resp = mapper.toResponse(inventory);

        assertNotNull(resp);
        assertEquals(21L, resp.getWarehouseId());
        assertEquals("Depot Z", resp.getWarehouseName());
        assertEquals(42L, resp.getProductId());
        assertEquals("Produit Y", resp.getProductName());
        assertEquals("SKU-Y", resp.getProductSku());
        assertEquals(100, resp.getQtyOnHand());
        assertEquals(12, resp.getQtyReserved());
    }

    @Test
    void updateEntityFromDto_ignoresNulls_and_preservesIdAndInventoryMovements() {
        Inventory inventory = new Inventory();
        inventory.setId(9L);
        inventory.setName("Ancien Stock");
        inventory.setQtyOnHand(50);
        inventory.setQtyReserved(5);
        inventory.setInventoryMovements(new ArrayList<>()); // doit rester inchangé

        InventoryRequest req = new InventoryRequest();
        req.setName("Nouveau Stock");
        req.setQtyOnHand(null); // doit être ignoré par NullValuePropertyMappingStrategy.IGNORE

        mapper.updateEntityFromDto(req, inventory);

        // id inchangé
        assertEquals(9L, inventory.getId());
        // name mis à jour
        assertEquals("Nouveau Stock", inventory.getName());
        // qtyOnHand inchangé car la valeur fournie était null
        assertEquals(50, inventory.getQtyOnHand());
        // inventoryMovements inchangé
        assertNotNull(inventory.getInventoryMovements());
    }
}
