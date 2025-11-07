package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.LoginRequest;
import com.example.StockFlow.dto.request.RegisterRequest;
import com.example.StockFlow.dto.response.AuthResponse;
import com.example.StockFlow.entity.User;
import com.example.StockFlow.exception.CustomException;
import com.example.StockFlow.mapper.UserMapper;
import com.example.StockFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    // Map pour stocker les sessions (token → user)
    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    // --- Inscription ---
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email déjà utilisé");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(encodePassword(request.getPassword()));
        userRepository.save(user);

        // Générer un token pour session immédiate si souhaité
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);

        return AuthResponse.builder()
                .message("Inscription réussie")
                .sessionId(token)
                .user(userMapper.toResponse(user))
                .build();
    }

    // --- Connexion ---
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Utilisateur non trouvé"));
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);

        return AuthResponse.builder()
                .message("Connexion réussie")
                .sessionId(token)
                .user(userMapper.toResponse(user))
                .build();
    }

    private String encodePassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de l'encodage du mot de passe", e);
        }
    }

    private boolean checkPassword(String rawPassword, String encodedPassword) {
        return encodePassword(rawPassword).equals(encodedPassword);
    }

    // --- Récupérer l'utilisateur courant ---
    public AuthResponse getCurrentUser(String token) {
        User user = sessions.get(token);
        if (user == null) {
            throw new CustomException("Token invalide ou expiré");
        }
        return AuthResponse.builder()
                .message("Utilisateur récupéré")
                .sessionId(token)
                .user(userMapper.toResponse(user))
                .build();
    }

    // --- Déconnexion ---
    public void logout(String token) {
        sessions.remove(token);
    }
}
