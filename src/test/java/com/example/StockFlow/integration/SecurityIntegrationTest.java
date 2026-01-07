package com.example.StockFlow.integration;

import com.example.StockFlow.dto.request.LoginRequest;
import com.example.StockFlow.dto.request.RegisterRequest;
import com.example.StockFlow.dto.response.AuthResponse;
import com.example.StockFlow.entity.RefreshToken;
import com.example.StockFlow.entity.User;
import com.example.StockFlow.entity.enums.Role;
import com.example.StockFlow.repository.RefreshTokenRepository;
import com.example.StockFlow.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour le module de sécurité
 *
 * Objectif: Vérifier le bon fonctionnement réel du module sécurité
 *
 * Périmètre:
 * - Authentification
 * - Refresh token
 * - Autorisation par rôle
 * - Isolation des données clients
 *
 * Critères de validation:
 * - Aucun endpoint protégé accessible sans token
 * - Les règles de sécurité sont strictement respectées
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests d'Intégration - Sécurité")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User adminUser;
    private User clientUser;
    private User warehouseManagerUser;

    @BeforeEach
    void setUp() {
        // Nettoyer la base
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        // Créer des utilisateurs de test
        adminUser = User.builder()
                .username("admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode("adminPass"))
                .role(Role.ADMIN)
                .actif(true)
                .build();
        userRepository.save(adminUser);

        clientUser = User.builder()
                .username("client")
                .email("client@test.com")
                .password(passwordEncoder.encode("clientPass"))
                .role(Role.CLIENT)
                .actif(true)
                .addressse("123 Client Street")
                .tel("0123456789")
                .build();
        userRepository.save(clientUser);

        warehouseManagerUser = User.builder()
                .username("warehouse_manager")
                .email("wm@test.com")
                .password(passwordEncoder.encode("wmPass"))
                .role(Role.WAREHOUSE_MANAGER)
                .actif(true)
                .build();
        userRepository.save(warehouseManagerUser);
    }

    // ==================== 1. TESTS D'AUTHENTIFICATION ====================

    @Test
    @DisplayName("1.1 - Login valide - doit retourner un token et refresh token")
    void testLoginValid_shouldReturnTokens() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("admin@test.com")
                .password("adminPass")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("admin@test.com"))
                .andExpect(jsonPath("$.user.role").value("ADMIN"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);

        assertThat(authResponse.getToken()).isNotNull();
        assertThat(authResponse.getRefreshToken()).isNotNull();
        assertThat(authResponse.getUser().getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    @DisplayName("1.2 - Login invalide - mauvais mot de passe doit retourner 401")
    void testLoginInvalid_wrongPassword_shouldReturn401() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("admin@test.com")
                .password("wrongPassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    @DisplayName("1.3 - Login invalide - email inexistant doit retourner 401")
    void testLoginInvalid_emailNotFound_shouldReturn401() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("nonexistent@test.com")
                .password("anyPassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("1.4 - Login avec utilisateur inactif doit échouer")
    void testLoginInactiveUser_shouldFail() throws Exception {
        // Désactiver l'utilisateur
        adminUser.setActif(false);
        userRepository.save(adminUser);

        LoginRequest request = LoginRequest.builder()
                .email("admin@test.com")
                .password("adminPass")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ==================== 2. TESTS D'ACCÈS AVEC TOKEN ====================

    @Test
    @DisplayName("2.1 - Accès endpoint protégé avec token valide - doit réussir")
    void testAccessProtectedEndpoint_withValidToken_shouldSucceed() throws Exception {
        String token = loginAndGetToken("admin@test.com", "adminPass");

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("admin@test.com"));
    }

    @Test
    @DisplayName("2.2 - Accès endpoint protégé sans token - doit retourner 401")
    void testAccessProtectedEndpoint_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/auth/me"));
    }

    @Test
    @DisplayName("2.3 - Accès endpoint protégé avec token invalide - doit retourner 401")
    void testAccessProtectedEndpoint_withInvalidToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer invalidToken123"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("2.4 - Accès endpoint protégé avec token malformé - doit retourner 401")
    void testAccessProtectedEndpoint_withMalformedToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "InvalidFormat"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== 3. TESTS DE REFRESH TOKEN ====================

    @Test
    @DisplayName("3.1 - Refresh token valide - doit retourner nouveau token")
    void testRefreshToken_valid_shouldReturnNewToken() throws Exception {
        // Login pour obtenir refresh token
        String refreshToken = loginAndGetRefreshToken("admin@test.com", "adminPass");

        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    @DisplayName("3.2 - Refresh token invalide - doit retourner 400")
    void testRefreshToken_invalid_shouldReturn400() throws Exception {
        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", "invalidRefreshToken");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("3.3 - Refresh token expiré - doit être rejeté")
    void testRefreshToken_expired_shouldBeRejected() throws Exception {
        // Créer un refresh token expiré
        RefreshToken expiredToken = RefreshToken.builder()
                .user(adminUser)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().minusDays(1))
                .build();
        refreshTokenRepository.save(expiredToken);

        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", expiredToken.getToken());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("3.4 - Refresh token révoqué - doit être rejeté")
    void testRefreshToken_revoked_shouldBeRejected() throws Exception {
        // Créer et sauvegarder un refresh token
        RefreshToken token = RefreshToken.builder()
                .user(adminUser)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(token);

        // Révoquer le token (supprimer)
        refreshTokenRepository.delete(token);

        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", token.getToken());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }

    // ==================== 4. TESTS D'AUTORISATION PAR RÔLE ====================

    @Test
    @DisplayName("4.1 - Admin peut créer des utilisateurs")
    void testAdmin_canRegisterUsers() throws Exception {
        String token = loginAndGetToken("admin@test.com", "adminPass");

        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@test.com")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("4.2 - Client ne peut pas créer des utilisateurs - doit retourner 403")
    void testClient_cannotRegisterUsers_shouldReturn403() throws Exception {
        String token = loginAndGetToken("client@test.com", "clientPass");

        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@test.com")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/auth/register"));
    }

    @Test
    @DisplayName("4.3 - Warehouse Manager ne peut pas créer des utilisateurs - doit retourner 403")
    void testWarehouseManager_cannotRegisterUsers_shouldReturn403() throws Exception {
        String token = loginAndGetToken("wm@test.com", "wmPass");

        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@test.com")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    @DisplayName("4.4 - Warehouse Manager peut accéder aux inventaires")
    void testWarehouseManager_canAccessInventory() throws Exception {
        String token = loginAndGetToken("wm@test.com", "wmPass");

        mockMvc.perform(get("/api/inventories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("4.5 - Client ne peut pas accéder aux inventaires - doit retourner 403")
    void testClient_cannotAccessInventory_shouldReturn403() throws Exception {
        String token = loginAndGetToken("client@test.com", "clientPass");

        mockMvc.perform(get("/api/inventories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    @DisplayName("4.6 - Admin peut accéder à toutes les ressources")
    void testAdmin_canAccessAllResources() throws Exception {
        String token = loginAndGetToken("admin@test.com", "adminPass");

        // Peut accéder aux inventaires
        mockMvc.perform(get("/api/inventories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Peut accéder aux produits
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // ==================== 5. TESTS D'ISOLATION DES DONNÉES CLIENTS ====================

    @Test
    @DisplayName("5.1 - Client ne peut voir que ses propres données")
    void testClient_canOnlySeeOwnData() throws Exception {
        // Créer deux clients
        User client1 = User.builder()
                .username("client1")
                .email("client1@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(Role.CLIENT)
                .actif(true)
                .build();
        userRepository.save(client1);

        User client2 = User.builder()
                .username("client2")
                .email("client2@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(Role.CLIENT)
                .actif(true)
                .build();
        userRepository.save(client2);

        String token1 = loginAndGetToken("client1@test.com", "pass");

        // Client1 accède à ses infos
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("client1@test.com"));
    }

    // ==================== 6. TESTS DE VALIDATION DES CRITÈRES ====================

    @Test
    @DisplayName("6.1 - Tous les endpoints protégés sans token retournent 401")
    void testAllProtectedEndpoints_withoutToken_return401() throws Exception {
        // Test divers endpoints
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/products")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/inventories")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("6.2 - Les règles de sécurité sont strictement respectées")
    void testSecurityRules_areStrictlyEnforced() throws Exception {
        String clientToken = loginAndGetToken("client@test.com", "clientPass");
        String wmToken = loginAndGetToken("wm@test.com", "wmPass");
        String adminToken = loginAndGetToken("admin@test.com", "adminPass");

        // CLIENT ne peut pas accéder aux inventaires
        mockMvc.perform(get("/api/inventories")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());

        // WM ne peut pas créer d'utilisateurs
        RegisterRequest request = RegisterRequest.builder()
                .username("test")
                .email("test@test.com")
                .password("pass")
                .role(Role.CLIENT)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer " + wmToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // ADMIN peut tout faire
        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    private String loginAndGetToken(String email, String password) throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        return authResponse.getToken();
    }

    private String loginAndGetRefreshToken(String email, String password) throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        return authResponse.getRefreshToken();
    }
}
