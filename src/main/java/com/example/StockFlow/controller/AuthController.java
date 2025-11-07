package com.example.StockFlow.controller;

import com.example.StockFlow.dto.request.LoginRequest;
import com.example.StockFlow.dto.request.RegisterRequest;
import com.example.StockFlow.dto.response.AuthResponse;
import com.example.StockFlow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // --- Inscription ---
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // --- Connexion ---
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // --- Récupérer l'utilisateur courant ---
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.getCurrentUser(token));
    }

    // --- Déconnexion ---
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Déconnecté avec succès");
    }
}
