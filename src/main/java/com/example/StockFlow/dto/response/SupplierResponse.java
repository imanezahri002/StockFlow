package com.example.StockFlow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierResponse {

    private Long id;
    private String companyName;
    private String address;
    private String phoneNumber;
    private String email;
    private String website;
    private String icf;
}
