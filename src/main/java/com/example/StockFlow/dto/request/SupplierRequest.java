package com.example.StockFlow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierRequest {

    @NotBlank(message = "Le nom de l'entreprise est obligatoire")
    @Size(max = 255, message = "Le nom de l'entreprise ne peut pas dépasser 255 caractères")
    private String companyName;

    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String address;

    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    private String phoneNumber;

    @Email(message = "L'email doit être valide")
    @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères")
    private String email;

    @Size(max = 255, message = "Le site web ne peut pas dépasser 255 caractères")
    private String website;

    @Size(max = 20, message = "Le code ICF ne peut pas dépasser 20 caractères")
    private String icf;
}
