// java
package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.PurchaseOrderLineRequest;
import com.example.StockFlow.dto.request.PurchaseOrderRequest;
import com.example.StockFlow.dto.response.AuthResponse;
import com.example.StockFlow.dto.response.PurchaseOrderResponse;
import com.example.StockFlow.dto.response.UserResponse;
import com.example.StockFlow.entity.*;
import com.example.StockFlow.entity.enums.PurchaseOrderStatus;
import com.example.StockFlow.mapper.PurchaseOrderLineMapper;
import com.example.StockFlow.mapper.PurchaseOrderMapper;
import com.example.StockFlow.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurchaseOrderMapper purchaseOrderMapper;
    @Mock
    private PurchaseOrderLineMapper purchaseOrderLineMapper;
    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private AuthService authService;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    @Test
    void createOrder_success_savesOrderAndReturnsResponse() {
        // Arrange
        String token = "token";
        long managerId = 1L;
        UserResponse userResponse = new UserResponse();
        userResponse.setId(managerId);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUser(userResponse);
        when(authService.getCurrentUser(token)).thenReturn(authResponse);

        Manager manager = new Manager();
        manager.setId(managerId);
        when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));

        Supplier supplier = Supplier.builder().id(5L).build();
        PurchaseOrderRequest request = new PurchaseOrderRequest();
        request.setSupplierId(supplier.getId());
        request.setWarehouseId(2L);
        PurchaseOrderLineRequest lineReq = new PurchaseOrderLineRequest();
        lineReq.setProductId(10L);
        lineReq.setQuantity(3);
        request.setOrderLines(List.of(lineReq));

        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));

        Product product = Product.builder().id(10L).name("Prod").originalPrice(BigDecimal.valueOf(2)).build();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // mapper -> entity minimal
        when(purchaseOrderMapper.toEntity(any(PurchaseOrderRequest.class), eq(supplier), eq(manager)))
                .thenAnswer(inv -> new PurchaseOrder());

        when(purchaseOrderLineMapper.toEntity(any(PurchaseOrderLineRequest.class), eq(product)))
                .thenAnswer(inv -> {
                    PurchaseOrderLineRequest r = inv.getArgument(0);
                    PurchaseOrderLine line = PurchaseOrderLine.builder()
                            .product(product)
                            .quantity(r.getQuantity())
                            .build();
                    return line;
                });

        when(purchaseOrderMapper.toResponse(any(PurchaseOrder.class))).thenReturn(new PurchaseOrderResponse());

        // Act
        PurchaseOrderResponse response = purchaseOrderService.createOrder(request, token);

        // Assert
        assertNotNull(response);
        verify(authService).getCurrentUser(token);
        verify(managerRepository).findById(managerId);
        verify(supplierRepository).findById(supplier.getId());
        verify(productRepository).findById(product.getId());
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        verify(purchaseOrderMapper).toResponse(any(PurchaseOrder.class));
    }

    @Test
    void createOrder_productNotFound_throwsRuntimeException() {
        // Arrange
        String token = "token2";
        long managerId = 2L;

        // Mock authService
        UserResponse userResponse = new UserResponse();
        userResponse.setId(managerId);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUser(userResponse);
        when(authService.getCurrentUser(token)).thenReturn(authResponse);

        // Mock managerRepository
        Manager manager = new Manager();
        manager.setId(managerId);
        when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));

        // Mock supplierRepository
        Supplier supplier = Supplier.builder().id(7L).build();
        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));

        // Préparer request avec un produit inexistant
        PurchaseOrderLineRequest lineReq = new PurchaseOrderLineRequest();
        lineReq.setProductId(999L); // produit absent
        lineReq.setQuantity(1);

        PurchaseOrderRequest request = new PurchaseOrderRequest();
        request.setSupplierId(supplier.getId());
        request.setWarehouseId(2L);
        request.setOrderLines(List.of(lineReq));

        // Mock productRepository pour retourner vide
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Mock mapper pour ne pas bloquer le service
        when(purchaseOrderMapper.toEntity(any(PurchaseOrderRequest.class), eq(supplier), eq(manager)))
                .thenAnswer(inv -> new PurchaseOrder());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> purchaseOrderService.createOrder(request, token));

        // Vérifie que l'exception a été lancée
        assertNotNull(ex);

        // Vérifie que productRepository.findById a été appelé
        verify(productRepository).findById(999L);

        // Vérifie que purchaseOrderRepository.save n'a jamais été appelé
        verify(purchaseOrderRepository, never()).save(any(PurchaseOrder.class));
    }

    @Test
    void approvePurchaseOrder_whenNotApproved_createsInventoryAndMovement_andApprovesOrder() {
        // Arrange (identique au test existant)
        Product product = Product.builder().id(1L).name("Prod").originalPrice(null).build();
        Warehouse warehouse = Warehouse.builder().id(2L).name("WH-A").build();

        PurchaseOrderLine line = PurchaseOrderLine.builder()
                .product(product)
                .quantity(5)
                .build();

        PurchaseOrder po = PurchaseOrder.builder()
                .id(10L)
                .warehouseId(warehouse.getId())
                .status(PurchaseOrderStatus.CREATED)
                .orderLines(List.of(line))
                .build();

        when(purchaseOrderRepository.findById(po.getId())).thenReturn(Optional.of(po));
        when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByWarehouseAndProduct(warehouse, product)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> {
            Inventory i = inv.getArgument(0);
            if (i.getId() == null) i.setId(100L);
            return i;
        });
        when(purchaseOrderMapper.toResponse(any(PurchaseOrder.class))).thenReturn(new PurchaseOrderResponse());

        // Act
        PurchaseOrderResponse response = purchaseOrderService.approvePurchaseOrder(po.getId());

        // Assert
        assertNotNull(response);
        verify(purchaseOrderRepository).findById(po.getId());
        verify(warehouseRepository).findById(warehouse.getId());
        verify(inventoryRepository, atLeast(2)).save(any(Inventory.class));
        verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        verify(purchaseOrderRepository).save(po);
        verify(purchaseOrderMapper).toResponse(po);
        assertEquals(PurchaseOrderStatus.APPROVED, po.getStatus());
    }

    @Test
    void approvePurchaseOrder_whenAlreadyApproved_throwsRuntimeException() {
        // Arrange (identique au test existant)
        PurchaseOrder po = PurchaseOrder.builder()
                .id(20L)
                .status(PurchaseOrderStatus.APPROVED)
                .build();

        when(purchaseOrderRepository.findById(po.getId())).thenReturn(Optional.of(po));

        // Act / Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> purchaseOrderService.approvePurchaseOrder(po.getId()));
        assertTrue(ex.getMessage().toLowerCase().contains("already approved") || ex.getMessage().toLowerCase().contains("already"));
        verify(purchaseOrderRepository).findById(po.getId());
        verify(purchaseOrderRepository, never()).save(any(PurchaseOrder.class));
        verifyNoInteractions(inventoryRepository);
        verifyNoInteractions(inventoryMovementRepository);
    }
}
