// java
package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.ProductRequest;
import com.example.StockFlow.dto.response.ProductResponse;
import com.example.StockFlow.entity.*;
import com.example.StockFlow.entity.enums.OrderStatus;
import com.example.StockFlow.mapper.ProductMapper;
import com.example.StockFlow.repository.InventoryRepository;
import com.example.StockFlow.repository.ProductRepository;
import com.example.StockFlow.repository.SalesOrderRepository;
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
class ProductServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_whenSkuExists_throws() {
        ProductRequest req = mock(ProductRequest.class);
        when(req.getSku()).thenReturn("SKU1");
        when(productRepository.existsBySku("SKU1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(req));
        verify(productRepository).existsBySku("SKU1");
        verifyNoMoreInteractions(productMapper);
    }

    @Test
    void createProduct_success() {
        ProductRequest req = mock(ProductRequest.class);
        when(req.getSku()).thenReturn("SKU2");
        when(productRepository.existsBySku("SKU2")).thenReturn(false);

        Product toSave = Product.builder().sku("SKU2").build();
        Product saved = Product.builder().id(1L).sku("SKU2").build();
        ProductResponse resp = new ProductResponse();

        when(productMapper.toEntity(req)).thenReturn(toSave);
        when(productRepository.save(toSave)).thenReturn(saved);
        when(productMapper.toResponse(saved)).thenReturn(resp);

        ProductResponse result = productService.createProduct(req);

        assertEquals(resp, result);
        verify(productRepository).existsBySku("SKU2");
        verify(productMapper).toEntity(req);
        verify(productRepository).save(toSave);
        verify(productMapper).toResponse(saved);
    }

    @Test
    void getProductById_notFound_throws() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.getProductById(99L));
        verify(productRepository).findById(99L);
    }

    @Test
    void getProductById_success() {
        Product p = Product.builder().id(2L).sku("S").build();
        ProductResponse resp = new ProductResponse();
        when(productRepository.findById(2L)).thenReturn(Optional.of(p));
        when(productMapper.toResponse(p)).thenReturn(resp);

        ProductResponse out = productService.getProductById(2L);
        assertEquals(resp, out);
        verify(productRepository).findById(2L);
        verify(productMapper).toResponse(p);
    }

    @Test
    void getProductBySku_notFound_throws() {
        when(productRepository.findBySku("NO")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.getProductBySku("NO"));
        verify(productRepository).findBySku("NO");
    }

    @Test
    void getProductBySku_success() {
        Product p = Product.builder().sku("SKUX").build();
        ProductResponse resp = new ProductResponse();
        when(productRepository.findBySku("SKUX")).thenReturn(Optional.of(p));
        when(productMapper.toResponse(p)).thenReturn(resp);

        ProductResponse out = productService.getProductBySku("SKUX");
        assertEquals(resp, out);
        verify(productRepository).findBySku("SKUX");
        verify(productMapper).toResponse(p);
    }

    @Test
    void getAllProducts_and_filters() {
        Product a = new Product(); Product b = new Product();
        ProductResponse ra = new ProductResponse(); ProductResponse rb = new ProductResponse();

        when(productRepository.findAll()).thenReturn(List.of(a, b));
        when(productMapper.toResponseList(List.of(a, b))).thenReturn(List.of(ra, rb));
        List<ProductResponse> all = productService.getAllProducts();
        assertEquals(2, all.size());
        verify(productRepository).findAll();

        when(productRepository.findByCategory("cat")).thenReturn(List.of(a));
        when(productMapper.toResponseList(List.of(a))).thenReturn(List.of(ra));
        List<ProductResponse> byCat = productService.getProductsByCategory("cat");
        assertEquals(1, byCat.size());
        verify(productRepository).findByCategory("cat");

        when(productRepository.findByActive(true)).thenReturn(List.of(b));
        when(productMapper.toResponseList(List.of(b))).thenReturn(List.of(rb));
        List<ProductResponse> byActive = productService.getProductsByActiveStatus(true);
        assertEquals(1, byActive.size());
        verify(productRepository).findByActive(true);

        when(productRepository.findByNameContainingIgnoreCase("name")).thenReturn(List.of(a));
        when(productMapper.toResponseList(List.of(a))).thenReturn(List.of(ra));
        List<ProductResponse> byName = productService.searchProductsByName("name");
        assertEquals(1, byName.size());
        verify(productRepository).findByNameContainingIgnoreCase("name");
    }

    @Test
    void updateProduct_notFound_throws() {
        when(productRepository.findById(5L)).thenReturn(Optional.empty());
        ProductRequest req = mock(ProductRequest.class);
        assertThrows(RuntimeException.class, () -> productService.updateProduct(5L, req));
        verify(productRepository).findById(5L);
    }

    @Test
    void updateProduct_skuConflict_throws() {
        Product existing = Product.builder().id(6L).sku("OLD").build();
        ProductRequest req = mock(ProductRequest.class);
        when(productRepository.findById(6L)).thenReturn(Optional.of(existing));
        when(req.getSku()).thenReturn("NEW");
        when(productRepository.existsBySku("NEW")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(6L, req));
        verify(productRepository).findById(6L);
        verify(productRepository).existsBySku("NEW");
    }

    @Test
    void updateProduct_success() {
        Product existing = Product.builder().id(7L).sku("SAME").build();
        ProductRequest req = mock(ProductRequest.class);
        when(productRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(req.getSku()).thenReturn("SAME"); // unchanged -> no existsBySku call
        doNothing().when(productMapper).updateEntityFromDTO(req, existing);
        Product saved = Product.builder().id(7L).sku("SAME").build();
        ProductResponse resp = new ProductResponse();
        when(productRepository.save(existing)).thenReturn(saved);
        when(productMapper.toResponse(saved)).thenReturn(resp);

        ProductResponse out = productService.updateProduct(7L, req);
        assertEquals(resp, out);
        verify(productRepository).findById(7L);
        verify(productMapper).updateEntityFromDTO(req, existing);
        verify(productRepository).save(existing);
    }

    @Test
    void deleteProduct_notFound_throws() {
        when(productRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> productService.deleteProduct(99L));
        verify(productRepository).existsById(99L);
    }

    @Test
    void deleteProduct_success() {
        when(productRepository.existsById(3L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(3L);
        productService.deleteProduct(3L);
        verify(productRepository).deleteById(3L);
    }

    @Test
    void deactivateProduct_notFound_throws() {
        when(productRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.deactivateProduct(100L));
        verify(productRepository).findById(100L);
    }

    @Test
    void deactivateProduct_success() {
        Product p = Product.builder().id(4L).active(true).build();
        Product saved = Product.builder().id(4L).active(false).build();
        ProductResponse resp = new ProductResponse();
        when(productRepository.findById(4L)).thenReturn(Optional.of(p));
        when(productRepository.save(p)).thenReturn(saved);
        when(productMapper.toResponse(saved)).thenReturn(resp);

        ProductResponse out = productService.deactivateProduct(4L);
        assertEquals(resp, out);
        verify(productRepository).save(p);
        verify(productMapper).toResponse(saved);
    }

    @Test
    void desactiverProductBySku_notFound_throws() {
        when(productRepository.findBySku("X")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.desactiverProductBySku("X"));
        verify(productRepository).findBySku("X");
    }

    @Test
    void desactiverProductBySku_referencedInOrder_throws() {
        String sku = "REFSKU";
        Product p = Product.builder().sku(sku).build();
        SalesOrderLine line = SalesOrderLine.builder().product(p).build();
        SalesOrder order = SalesOrder.builder().status(OrderStatus.CREATED).orderLines(List.of(line)).build();

        when(productRepository.findBySku(sku)).thenReturn(Optional.of(p));
        when(salesOrderRepository.findAll()).thenReturn(List.of(order));

        assertThrows(RuntimeException.class, () -> productService.desactiverProductBySku(sku));
        verify(salesOrderRepository).findAll();
    }

    @Test
    void desactiverProductBySku_qtyReserved_throws() {
        String sku = "RSKU";
        Product p = Product.builder().sku(sku).build();
        Inventory inv = Inventory.builder().qtyReserved(2).build();

        when(productRepository.findBySku(sku)).thenReturn(Optional.of(p));
        when(salesOrderRepository.findAll()).thenReturn(List.of());
        when(inventoryRepository.findByProduct(p)).thenReturn(List.of(inv));

        assertThrows(RuntimeException.class, () -> productService.desactiverProductBySku(sku));
        verify(inventoryRepository).findByProduct(p);
    }

    @Test
    void desactiverProductBySku_success() {
        String sku = "OKSKU";
        Product p = Product.builder().sku(sku).active(true).build();
        Inventory inv = Inventory.builder().qtyReserved(0).build();
        Product saved = Product.builder().sku(sku).active(false).build();
        ProductResponse resp = new ProductResponse();

        when(productRepository.findBySku(sku)).thenReturn(Optional.of(p));
        when(salesOrderRepository.findAll()).thenReturn(List.of());
        when(inventoryRepository.findByProduct(p)).thenReturn(List.of(inv));
        when(productRepository.save(p)).thenReturn(saved);
        when(productMapper.toResponse(saved)).thenReturn(resp);

        ProductResponse out = productService.desactiverProductBySku(sku);
        assertEquals(resp, out);
        verify(productRepository).save(p);
        verify(productMapper).toResponse(saved);
    }

    @Test
    void activateProduct_notFound_throws() {
        when(productRepository.findById(55L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.activateProduct(55L));
        verify(productRepository).findById(55L);
    }

    @Test
    void activateProduct_success() {
        Product p = Product.builder().id(8L).active(false).build();
        Product saved = Product.builder().id(8L).active(true).build();
        ProductResponse resp = new ProductResponse();

        when(productRepository.findById(8L)).thenReturn(Optional.of(p));
        when(productRepository.save(p)).thenReturn(saved);
        when(productMapper.toResponse(saved)).thenReturn(resp);

        ProductResponse out = productService.activateProduct(8L);
        assertEquals(resp, out);
        verify(productRepository).save(p);
        verify(productMapper).toResponse(saved);
    }
}
