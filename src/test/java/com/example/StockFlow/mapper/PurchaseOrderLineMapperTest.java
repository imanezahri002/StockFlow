// java
package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.PurchaseOrderLineRequest;
import com.example.StockFlow.dto.response.PurchaseOrderLineResponse;
import com.example.StockFlow.entity.Product;
import com.example.StockFlow.entity.PurchaseOrderLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseOrderLineMapperTest {

    private PurchaseOrderLineMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PurchaseOrderLineMapper.class);
    }

    @Test
    void toEntity_shouldIgnoreId_andSetProduct() {
        PurchaseOrderLineRequest req = new PurchaseOrderLineRequest();
        // remplir des champs du request si nécessaire (non requis pour ce test)
        Product product = new Product();
        product.setId(100L);
        product.setName("Test Product");

        PurchaseOrderLine entity = mapper.toEntity(req, product);

        assertNotNull(entity);
        // id doit être ignoré par le mapper
        assertNull(entity.getId(), "L'id de PurchaseOrderLine doit être ignoré par le mapping");
        // le product fourni doit être référencé
        assertSame(product, entity.getProduct());
        // champs marqués ignore (unitPrice, totalPrice, createdAt) peuvent rester null
    }

    @Test
    void toResponse_shouldMapProductName() {
        Product product = new Product();
        product.setId(200L);
        product.setName("Produit 200");

        PurchaseOrderLine entity = new PurchaseOrderLine();
        entity.setProduct(product);

        PurchaseOrderLineResponse resp = mapper.toResponse(entity);

        assertNotNull(resp);
        assertEquals("Produit 200", resp.getProductName());
    }
}
