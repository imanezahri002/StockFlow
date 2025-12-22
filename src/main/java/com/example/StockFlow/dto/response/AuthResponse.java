package com.example.StockFlow.dto.response;

import com.example.StockFlow.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private String token;
    private UserResponse user;
    private String refreshToken;
}
