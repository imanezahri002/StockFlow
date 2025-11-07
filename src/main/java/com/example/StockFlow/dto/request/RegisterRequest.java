package com.example.StockFlow.dto.request;


import com.example.StockFlow.entity.enums.Role;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
    private String addressse; // pour Client
    private String tel;
}
