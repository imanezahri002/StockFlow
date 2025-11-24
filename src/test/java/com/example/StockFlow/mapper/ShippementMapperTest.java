// java
package com.example.StockFlow.mapper;

import com.example.StockFlow.dto.request.ShippementRequest;
import com.example.StockFlow.dto.response.ShippementResponse;
import com.example.StockFlow.entity.Shippement;
import com.example.StockFlow.entity.Carrier;
import com.example.StockFlow.entity.SalesOrder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ShippementMapperTest {

    private final ShippementMapper mapper = Mappers.getMapper(ShippementMapper.class);

    @Test
    void toResponse_mapsNestedCarrierAndSalesOrder() {
        Carrier carrier = new Carrier();
        carrier.setId(10L);
        carrier.setName("UPS");

        SalesOrder order = new SalesOrder();
        order.setId(100L);

        Shippement shipment = new Shippement();
        shipment.setId(1L);
        shipment.setCarrier(carrier);
        shipment.setSalesOrder(order);

        ShippementResponse resp = mapper.toResponse(shipment);

        assertNotNull(resp);
        assertNotNull(resp.getCarrier());
        assertEquals(10L, resp.getCarrier().getId());
        assertEquals("UPS", resp.getCarrier().getName());
        assertNotNull(resp.getSalesOrder());
        assertEquals(100L, resp.getSalesOrder().getId());
    }

    @Test
    void toEntity_ignoresCarrierAndSalesOrder() {
        ShippementRequest req = new ShippementRequest();
        // Le mapper ignore carrier et salesOrder lors de la conversion
        Shippement entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertNull(entity.getCarrier());
        assertNull(entity.getSalesOrder());
    }

    @Test
    void updateEntityFromRequest_keepsIdCarrierAndSalesOrder() {
        ShippementRequest req = new ShippementRequest();
        // remplir des champs modifiables du request si nécessaire

        Shippement shipment = new Shippement();
        shipment.setId(1L);

        Carrier carrier = new Carrier();
        carrier.setId(20L);
        carrier.setName("DHL");

        SalesOrder order = new SalesOrder();
        order.setId(200L);

        shipment.setCarrier(carrier);
        shipment.setSalesOrder(order);

        mapper.updateEntityFromRequest(req, shipment);

        assertEquals(1L, shipment.getId());
        assertSame(carrier, shipment.getCarrier());
        assertSame(order, shipment.getSalesOrder());
    }
}
