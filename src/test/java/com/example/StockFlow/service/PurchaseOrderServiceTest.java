// java
package com.example.StockFlow.service;

import com.example.StockFlow.dto.response.PurchaseOrderResponse;
import com.example.StockFlow.entity.*;
import com.example.StockFlow.entity.enums.MovementType;
import com.example.StockFlow.entity.enums.PurchaseOrderStatus;
import com.example.StockFlow.mapper.PurchaseOrderMapper;
import com.example.StockFlow.mapper.PurchaseOrderLineMapper;
import com.example.StockFlow.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void approvePurchaseOrder_whenNotApproved_createsInventoryAndMovement_andApprovesOrder() {
        // Arrange
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
        // Simuler absence d'inventaire -> création via save
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
        // Au moins deux saves sur inventory : création + mise à jour qty
        verify(inventoryRepository, atLeast(2)).save(any(Inventory.class));
        verify(inventoryMovementRepository, times(1)).save(any(InventoryMovement.class));
        verify(purchaseOrderRepository).save(po);
        verify(purchaseOrderMapper).toResponse(po);
        assertEquals(PurchaseOrderStatus.APPROVED, po.getStatus());
    }

    @Test
    void approvePurchaseOrder_whenAlreadyApproved_throwsRuntimeException() {
        // Arrange
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
