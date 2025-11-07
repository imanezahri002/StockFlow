package com.example.StockFlow.entity;


import com.example.StockFlow.entity.enums.CarrierStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private String email;
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    private CarrierStatus status;
}
