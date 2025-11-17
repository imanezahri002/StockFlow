// java
package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.CarrierRequest;
import com.example.StockFlow.dto.response.CarrierResponse;
import com.example.StockFlow.entity.Carrier;
import com.example.StockFlow.exception.CustomException;
import com.example.StockFlow.mapper.CarrierMapper;
import com.example.StockFlow.repository.CarrierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarrierService {

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    public CarrierResponse createCarrier(CarrierRequest request) {
        Carrier carrier = carrierMapper.toEntity(request);
        carrier.setId(null);
        return carrierMapper.toResponse(carrierRepository.save(carrier));
    }

    public List<CarrierResponse> getAllCarriers() {
        return carrierRepository.findAll().stream()
                .map(carrierMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CarrierResponse getCarrierById(Long id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new CustomException("Carrier not found with ID: " + id));
        return carrierMapper.toResponse(carrier);
    }

    public CarrierResponse updateCarrier(Long id, CarrierRequest request) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new CustomException("Carrier not found with ID: " + id));

        // Utilise MapStruct pour mise à jour partielle (ignore les nulls, n'écrase pas l'id)
        carrierMapper.updateEntityFromDto(request, carrier);

        return carrierMapper.toResponse(carrierRepository.save(carrier));
    }

    public void deleteCarrier(Long id) {
        if (!carrierRepository.existsById(id)) {
            throw new CustomException("Carrier not found with ID: " + id);
        }
        carrierRepository.deleteById(id);
    }
}
