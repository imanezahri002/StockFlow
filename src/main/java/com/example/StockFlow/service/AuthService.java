package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.LoginRequest;
import com.example.StockFlow.dto.request.RegisterRequest;
import com.example.StockFlow.dto.response.AuthResponse;
import com.example.StockFlow.dto.response.UserResponse;
import com.example.StockFlow.entity.RefreshToken;
import com.example.StockFlow.entity.User;
import com.example.StockFlow.entity.enums.Role;
import com.example.StockFlow.jwt.JwtService;
import com.example.StockFlow.repository.RefreshTokenRepository;
import com.example.StockFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    // ================= REGISTER =================
    public AuthResponse register(RegisterRequest request) {

        // 1️⃣ Vérifier email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // 2️⃣ Valider le rôle envoyé
        Role role = validateRole(request.getRole());

        // 3️⃣ Créer utilisateur
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .tel(request.getTel())
                .addressse(request.getAddressse())
                .role(role)
                .actif(true)
                .build();

        // 4️⃣ Sauvegarder
        userRepository.save(user);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // 5️⃣ Générer JWT
        String token = jwtService.generateToken(user);

        // 6️⃣ Mapper User → UserResponse
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .tel(user.getTel())
                .addressse(user.getAddressse())
                .role(user.getRole().name())
                .actif(user.getActif())
                .build();

        // 7️⃣ Retourner réponse
        return AuthResponse.builder()
                .message("Inscription réussie")
                .token(token)
                .refreshToken(refreshToken.getToken())
                .user(userResponse)
                .build();
    }

    // ================= LOGIN =================
    public AuthResponse login(LoginRequest request) {

        // 1️⃣ Authentification Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2️⃣ Récupérer utilisateur
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 3️⃣ Générer JWT
        String token = jwtService.generateToken(user);

        // 4️⃣ Générer refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // 4️⃣ Mapper User → UserResponse
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();

        // 5️⃣ Retourner réponse
        return AuthResponse.builder()
                .message("Connexion réussie")
                .token(token)
                .refreshToken(refreshToken.getToken())
                .user(userResponse)
                .build();
    }
    // ================= GET CURRENT USER =================
    public AuthResponse getCurrentUser() {
        User user = getAuthenticatedUser(); // utilise la méthode interne

        UserResponse userResponse = mapToUserResponse(user);

        return AuthResponse.builder()
                .message("Utilisateur courant")
                .user(userResponse)
                .build();
    }

    // ================= UPDATE CURRENT USER =================
    public AuthResponse updateCurrentUser(RegisterRequest request) {
        User user = getAuthenticatedUser();

        // Mettre à jour les champs modifiables
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getTel() != null) {
            user.setTel(request.getTel());
        }
        if (request.getAddressse() != null) {
            user.setAddressse(request.getAddressse());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        UserResponse userResponse = mapToUserResponse(user);

        return AuthResponse.builder()
                .message("Profil mis à jour avec succès")
                .user(userResponse)
                .build();
    }

    // ================= REFRESH TOKEN =================
    public AuthResponse refreshToken(String refreshTokenStr) {
        // 1️⃣ Récupérer le refresh token de la BD
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token invalide"));

        // 2️⃣ Vérifier s'il est expiré
        refreshTokenService.verifyExpiration(refreshToken);

        // 3️⃣ Générer un nouveau JWT
        User user = refreshToken.getUser();
        String newToken = jwtService.generateToken(user);

        // 4️⃣ Mapper User → UserResponse
        UserResponse userResponse = mapToUserResponse(user);

        // 5️⃣ Retourner réponse avec nouveau token
        return AuthResponse.builder()
                .message("Token rafraîchi avec succès")
                .token(newToken)
                .refreshToken(refreshTokenStr) // on garde le même refresh token
                .user(userResponse)
                .build();
    }

    // ---------------- Helper pour récupérer l'utilisateur connecté ----------------
    public User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        throw new RuntimeException("Utilisateur non authentifié");
    }

    // ---------------- Helper pour mapper User → UserResponse ----------------
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .tel(user.getTel())
                .addressse(user.getAddressse())
                .role(user.getRole().name())
                .actif(user.getActif())
                .build();
    }

    // ================= ROLE VALIDATION =================
    private Role validateRole(Role role) {

        if (role == null) {
            throw new RuntimeException("Le rôle est obligatoire");
        }

        if (role == Role.ADMIN) {
            throw new RuntimeException("Création ADMIN interdite");
        }

        return role;
    }
}
