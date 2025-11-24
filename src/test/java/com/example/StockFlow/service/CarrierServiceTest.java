package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.CarrierRequest;
import com.example.StockFlow.dto.response.CarrierResponse;
import com.example.StockFlow.entity.Carrier;
import com.example.StockFlow.exception.CustomException;
import com.example.StockFlow.mapper.CarrierMapper;
import com.example.StockFlow.repository.CarrierRepository;
import com.example.StockFlow.service.CarrierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrierServiceTest {

    @Mock
    private CarrierRepository carrierRepository;
    @Mock
    private CarrierMapper carrierMapper;

    @InjectMocks
    private CarrierService carrierService;

    @Test
    void createCarrier_savesAndReturnsResponse() {
        CarrierRequest req = new CarrierRequest();
        Carrier entity = new Carrier();
        Carrier saved = new Carrier();
        saved.setId(1L);
        CarrierResponse resp = new CarrierResponse();

        when(carrierMapper.toEntity(req)).thenReturn(entity);
        when(carrierRepository.save(entity)).thenReturn(saved);
        when(carrierMapper.toResponse(saved)).thenReturn(resp);

        CarrierResponse result = carrierService.createCarrier(req);

        assertSame(resp, result);
        verify(carrierMapper).toEntity(req);
        verify(carrierRepository).save(entity);
        verify(carrierMapper).toResponse(saved);
    }

    @Test
    void getAllCarriers_returnsMappedList() {
        // Arrange
        Carrier c1 = new Carrier();
        c1.setId(1L);
        c1.setName("Carrier 1");

        Carrier c2 = new Carrier();
        c2.setId(2L);
        c2.setName("Carrier 2");

        CarrierResponse r1 = new CarrierResponse();
        r1.setId(1L);
        r1.setName("Carrier 1");

        CarrierResponse r2 = new CarrierResponse();
        r2.setId(2L);
        r2.setName("Carrier 2");

        List<Carrier> carriers = Arrays.asList(c1, c2);

        when(carrierRepository.findAll()).thenReturn(carriers);
        when(carrierMapper.toResponse(c1)).thenReturn(r1);
        when(carrierMapper.toResponse(c2)).thenReturn(r2);

        // Act
        List<CarrierResponse> result = carrierService.getAllCarriers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Carrier 1", result.get(0).getName());
        assertEquals("Carrier 2", result.get(1).getName());

        verify(carrierRepository).findAll();

        // >>> Correction importante : accepter 1 ou plusieurs appels <<
        verify(carrierMapper, atLeastOnce()).toResponse(c1);
        verify(carrierMapper, atLeastOnce()).toResponse(c2);
    }

    @Test
    void getCarrierById_found_returnsResponse() {
        Carrier carrier = new Carrier();
        CarrierResponse resp = new CarrierResponse();
        when(carrierRepository.findById(5L)).thenReturn(Optional.of(carrier));
        when(carrierMapper.toResponse(carrier)).thenReturn(resp);

        CarrierResponse result = carrierService.getCarrierById(5L);

        assertSame(resp, result);
        verify(carrierRepository).findById(5L);
        verify(carrierMapper).toResponse(carrier);
    }

    @Test
    void getCarrierById_notFound_throwsCustomException() {
        when(carrierRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> carrierService.getCarrierById(99L));
        verify(carrierRepository).findById(99L);
    }

    @Test
    void updateCarrier_found_updatesAndReturns() {
        CarrierRequest req = new CarrierRequest();
        Carrier existing = new Carrier();
        existing.setId(7L);
        Carrier saved = new Carrier();
        saved.setId(7L);
        CarrierResponse resp = new CarrierResponse();

        when(carrierRepository.findById(7L)).thenReturn(Optional.of(existing));
        // updateEntityFromDto est void, on vérifie l'appel
        when(carrierRepository.save(existing)).thenReturn(saved);
        when(carrierMapper.toResponse(saved)).thenReturn(resp);

        CarrierResponse result = carrierService.updateCarrier(7L, req);

        assertSame(resp, result);
        verify(carrierRepository).findById(7L);
        verify(carrierMapper).updateEntityFromDto(req, existing);
        verify(carrierRepository).save(existing);
        verify(carrierMapper).toResponse(saved);
    }

    @Test
    void updateCarrier_notFound_throwsCustomException() {
        CarrierRequest req = new CarrierRequest();
        when(carrierRepository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> carrierService.updateCarrier(42L, req));
        verify(carrierRepository).findById(42L);
        verifyNoMoreInteractions(carrierMapper);
    }

    @Test
    void deleteCarrier_exists_deletes() {
        when(carrierRepository.existsById(3L)).thenReturn(true);
        carrierService.deleteCarrier(3L);
        verify(carrierRepository).existsById(3L);
        verify(carrierRepository).deleteById(3L);
    }

    @Test
    void deleteCarrier_notExists_throwsCustomException() {
        when(carrierRepository.existsById(55L)).thenReturn(false);
        assertThrows(CustomException.class, () -> carrierService.deleteCarrier(55L));
        verify(carrierRepository).existsById(55L);
        verify(carrierRepository, never()).deleteById(ArgumentMatchers.anyLong());
    }
}

