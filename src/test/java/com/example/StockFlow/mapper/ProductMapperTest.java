// java
package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.ProductRequest;
import com.example.StockFlow.dto.response.ProductResponse;
import com.example.StockFlow.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProductMapper.class);
    }

    @Test
    void toResponse_mapsProductFields() {
        Product p = new Product();
        p.setId(1L);
        p.setName("Produit A");
        p.setSku("SKU-A");

        ProductResponse resp = mapper.toResponse(p);

        assertNotNull(resp);
        assertEquals("Produit A", resp.getName());
        assertEquals("SKU-A", resp.getSku());
        // si ProductResponse contient id, vérifier aussi (sécuritaire)
        if (resp.getId() != null) {
            assertEquals(1L, resp.getId());
        }
    }

    @Test
    void toResponseList_mapsAllProducts() {
        Product p1 = new Product(); p1.setName("P1"); p1.setSku("S1");
        Product p2 = new Product(); p2.setName("P2"); p2.setSku("S2");

        List<ProductResponse> list = mapper.toResponseList(Arrays.asList(p1, p2));

        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(r -> "P1".equals(r.getName()) && "S1".equals(r.getSku())));
        assertTrue(list.stream().anyMatch(r -> "P2".equals(r.getName()) && "S2".equals(r.getSku())));
    }

    @Test
    void toEntity_mapsRequestToEntity() {
        ProductRequest req = new ProductRequest();
        req.setName("New Product");
        req.setSku("NEW-SKU");

        Product entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertEquals("New Product", entity.getName());
        assertEquals("NEW-SKU", entity.getSku());
        // id normalement non fourni par le request -> peut rester null
        // on évite d'imposer une valeur d'id ici
    }

    @Test
    void updateEntityFromDTO_updatesNonNullAndIgnoresNulls() {
        Product product = new Product();
        product.setId(5L);
        product.setName("Ancien");
        product.setSku("OLD-SKU");

        ProductRequest req = new ProductRequest();
        req.setName("Nouveau");
        req.setSku(null); // doit être ignoré grâce à NullValuePropertyMappingStrategy.IGNORE

        mapper.updateEntityFromDTO(req, product);

        // id doit rester inchangé
        assertEquals(5L, product.getId());
        // name mis à jour
        assertEquals("Nouveau", product.getName());
        // sku inchangé car la valeur fournie était null
        assertEquals("OLD-SKU", product.getSku());
    }
}
