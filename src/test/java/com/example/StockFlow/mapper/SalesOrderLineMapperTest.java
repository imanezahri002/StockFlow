// java
package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.SalesOrderLineRequest;
import com.example.StockFlow.dto.response.SalesOrderLineResponse;
import com.example.StockFlow.entity.Product;
import com.example.StockFlow.entity.SalesOrderLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class SalesOrderLineMapperTest {

    private SalesOrderLineMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(SalesOrderLineMapper.class);
    }

    @Test
    void toEntity_shouldIgnoreId_andSetProduct() {
        SalesOrderLineRequest req = new SalesOrderLineRequest();
        // ne pas dépendre de champs incertains du request; on teste l'association product
        Product product = new Product();
        product.setId(100L);
        product.setName("Test Product");

        SalesOrderLine entity = mapper.toEntity(req, product);

        assertNotNull(entity);
        // id doit être ignoré par le mapping
        assertNull(entity.getId(), "L'id de SalesOrderLine doit être ignoré par le mapper");
        // le produit fourni doit être référencé directement
        assertSame(product, entity.getProduct());
    }

    @Test
    void toResponse_shouldMapProductIdAndName() {
        Product product = new Product();
        product.setId(200L);
        product.setName("Prod 200");

        SalesOrderLine entity = new SalesOrderLine();
        entity.setProduct(product);

        SalesOrderLineResponse resp = mapper.toResponse(entity);

        assertNotNull(resp);
        assertEquals(product.getId(), resp.getProductId());
        assertEquals(product.getName(), resp.getProductName());
    }
}
