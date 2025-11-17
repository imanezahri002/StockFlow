package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.ProductRequest;
import com.example.StockFlow.dto.response.ProductResponse;
import com.example.StockFlow.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

    private MockMvc mockMvc;
    private ProductService productService = Mockito.mock(ProductService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        ProductController controller = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createProduct_returnsCreated() throws Exception {
        ProductRequest req = new ProductRequest();
        req.setName("Laptop");
        req.setSku("SKU123");
        req.setActive(true);

        ProductResponse res = new ProductResponse();
        res.setId(1L);
        res.setName("Laptop");
        res.setSku("SKU123");
        res.setActive(true);

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService).createProduct(any(ProductRequest.class));
    }

    @Test
    void getProductById_returnsOk() throws Exception {
        ProductResponse res = new ProductResponse();
        res.setId(2L);
        res.setName("Phone");

        when(productService.getProductById(2L)).thenReturn(res);

        mockMvc.perform(get("/api/products/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Phone"));

        verify(productService).getProductById(2L);
    }

    @Test
    void getProductBySku_returnsOk() throws Exception {
        ProductResponse res = new ProductResponse();
        res.setSku("SKU999");
        res.setName("Tablet");

        when(productService.getProductBySku("SKU999")).thenReturn(res);

        mockMvc.perform(get("/api/products/sku/SKU999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU999"))
                .andExpect(jsonPath("$.name").value("Tablet"));

        verify(productService).getProductBySku("SKU999");
    }

    @Test
    void getAllProducts_defaultAndFilters() throws Exception {
        ProductResponse p1 = new ProductResponse(); p1.setId(1L); p1.setName("A");
        ProductResponse p2 = new ProductResponse(); p2.setId(2L); p2.setName("B");

        when(productService.getAllProducts()).thenReturn(List.of(p1, p2));
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        verify(productService).getAllProducts();

        reset(productService);

        when(productService.getProductsByCategory("cat")).thenReturn(List.of(p1));
        mockMvc.perform(get("/api/products").param("category", "cat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(productService).getProductsByCategory("cat");

        reset(productService);

        when(productService.getProductsByActiveStatus(true)).thenReturn(List.of(p2));
        mockMvc.perform(get("/api/products").param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(productService).getProductsByActiveStatus(true);

        reset(productService);

        when(productService.searchProductsByName("lap")).thenReturn(List.of(p1));
        mockMvc.perform(get("/api/products").param("search", "lap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(productService).searchProductsByName("lap");
    }

    @Test
    void updateProduct_returnsOk() throws Exception {
        ProductRequest req = new ProductRequest();
        req.setName("Updated");
        req.setSku("SKUUPD");
        req.setActive(true); // ajouté pour satisfaire la validation

        ProductResponse res = new ProductResponse();
        res.setId(5L);
        res.setName("Updated");
        res.setSku("SKUUPD");

        when(productService.updateProduct(eq(5L), any(ProductRequest.class))).thenReturn(res);

        mockMvc.perform(put("/api/products/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(productService).updateProduct(eq(5L), any(ProductRequest.class));
    }

    @Test
    void deleteProduct_returnsNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(7L);

        mockMvc.perform(delete("/api/products/7"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(7L);
    }

    @Test
    void deactivateProduct_returnsOk() throws Exception {
        ProductResponse res = new ProductResponse();
        res.setId(8L);
        res.setActive(false);

        when(productService.deactivateProduct(8L)).thenReturn(res);

        mockMvc.perform(patch("/api/products/8/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.active").value(false));

        verify(productService).deactivateProduct(8L);
    }

    @Test
    void desactiverProductBySku_returnsOk() throws Exception {
        ProductResponse res = new ProductResponse();
        res.setSku("BLOCKSKU");
        res.setActive(false);

        when(productService.desactiverProductBySku("BLOCKSKU")).thenReturn(res);

        mockMvc.perform(patch("/api/products/BLOCKSKU/blocProduct"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("BLOCKSKU"))
                .andExpect(jsonPath("$.active").value(false));

        verify(productService).desactiverProductBySku("BLOCKSKU");
    }

    @Test
    void activateProduct_returnsOk() throws Exception {
        ProductResponse res = new ProductResponse();
        res.setId(9L);
        res.setActive(true);

        when(productService.activateProduct(9L)).thenReturn(res);

        mockMvc.perform(patch("/api/products/9/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.active").value(true));

        verify(productService).activateProduct(9L);
    }
}
