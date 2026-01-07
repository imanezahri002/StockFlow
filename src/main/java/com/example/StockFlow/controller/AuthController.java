package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.LoginRequest;
import com.example.StockFlow.dto.request.RegisterRequest;
import com.example.StockFlow.dto.response.AuthResponse;
import com.example.StockFlow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // --- Inscription (ADMIN only - CREATE) ---
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // --- Connexion (Public) ---
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // --- Refresh Token (Public) ---
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    // --- Récupérer l'utilisateur courant (R - Profil: ADMIN, WM, CLIENT) ---
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    public ResponseEntity<AuthResponse> me() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    // --- Mettre à jour son profil (U - Profil: ADMIN, WM, CLIENT) ---
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    public ResponseEntity<AuthResponse> updateProfile(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.updateCurrentUser(request));
    }

}
