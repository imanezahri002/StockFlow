// java
package com.example.StockFlow.service;

import com.example.StockFlow.entity.*;
import com.example.StockFlow.exception.CustomException;
import com.example.StockFlow.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementService inventoryMovementService;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private com.example.StockFlow.mapper.SalesOrderMapper salesOrderMapper;

    @InjectMocks
    private SalesOrderService salesOrderService;

    @Test
    void reserveStock_allAvailable_returnsTrue_and_createsOutbound() {
        // Arrange
        Product product = Product.builder().id(1L).name("P1").build();
        Warehouse wh = Warehouse.builder().id(1L).name("WH1").build();
        Inventory inv = Inventory.builder().id(1L).warehouse(wh).product(product).qtyOnHand(10).qtyReserved(0).build();

        SalesOrder order = SalesOrder.builder().id(null).build();
        SalesOrderLine line = SalesOrderLine.builder().salesOrder(order).product(product).quantity(3).unitPrice(BigDecimal.TEN).totalPrice(BigDecimal.TEN.multiply(BigDecimal.valueOf(3))).backorder(false).build();
        order.setOrderLines(List.of(line));

        when(warehouseRepository.findAll()).thenReturn(List.of(wh));
        when(inventoryRepository.findByWarehouseAndProduct(wh, product)).thenReturn(Optional.of(inv));

        // Act
        Boolean result = (Boolean) ReflectionTestUtils.invokeMethod(salesOrderService, "reserveStock", order);

        // Assert
        assertTrue(result);
        assertFalse(line.isBackorder());
        verify(inventoryRepository, times(1)).save(inv);
        verify(inventoryMovementService, times(1)).createOutboundMovement(eq(inv), eq(3), anyString(), anyString());
    }

    @Test
    void reserveStock_partialInventory_setsBackorder_and_updatesQtyReserved() {
        // Arrange
        Product product = Product.builder().id(2L).name("P2").build();
        Warehouse wh = Warehouse.builder().id(2L).name("WH2").build();
        Inventory inv = Inventory.builder().id(2L).warehouse(wh).product(product).qtyOnHand(1).qtyReserved(2).build();

        SalesOrder order = SalesOrder.builder().build();
        SalesOrderLine line = SalesOrderLine.builder().salesOrder(order).product(product).quantity(5).unitPrice(BigDecimal.ONE).totalPrice(BigDecimal.ONE.multiply(BigDecimal.valueOf(5))).backorder(false).build();
        order.setOrderLines(List.of(line));

        when(warehouseRepository.findAll()).thenReturn(List.of(wh));
        when(inventoryRepository.findByWarehouseAndProduct(wh, product)).thenReturn(Optional.of(inv));

        // Act
        Boolean result = (Boolean) ReflectionTestUtils.invokeMethod(salesOrderService, "reserveStock", order);

        // Assert
        assertFalse(result);
        assertTrue(line.isBackorder());
        // fallbackInventory was inv => qtyReserved increased by remaining (5 - 1 = 4)
        assertEquals(6, inv.getQtyReserved()); // initial 2 + 4
        verify(inventoryRepository, atLeastOnce()).save(inv);
        verify(inventoryMovementService, times(1)).createOutboundMovement(eq(inv), eq(1), anyString(), anyString());
    }

    @Test
    void reserveStock_noInventory_throwsCustomException() {
        // Arrange
        Product product = Product.builder().id(3L).name("P3").build();
        Warehouse wh = Warehouse.builder().id(3L).name("WH3").build();

        SalesOrder order = SalesOrder.builder().build();
        SalesOrderLine line = SalesOrderLine.builder().salesOrder(order).product(product).quantity(2).unitPrice(BigDecimal.ONE).totalPrice(BigDecimal.ONE.multiply(BigDecimal.valueOf(2))).backorder(false).build();
        order.setOrderLines(List.of(line));

        when(warehouseRepository.findAll()).thenReturn(List.of(wh));
        when(inventoryRepository.findByWarehouseAndProduct(wh, product)).thenReturn(Optional.empty());

        // Act / Assert
        assertThrows(CustomException.class, () -> ReflectionTestUtils.invokeMethod(salesOrderService, "reserveStock", order));
    }
}
