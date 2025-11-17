package com.example.StockFlow.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void builderAndGetters_shouldReturnProvidedValues() {
        UserResponse user = new UserResponse();
        AuthResponse auth = AuthResponse.builder()
                .message("Succès")
                .sessionId("sess-123")
                .user(user)
                .build();

        assertEquals("Succès", auth.getMessage());
        assertEquals("sess-123", auth.getSessionId());
        assertSame(user, auth.getUser());
    }

    @Test
    void setters_shouldModifyValues() {
        UserResponse user = new UserResponse();
        AuthResponse auth = new AuthResponse();

        auth.setMessage("Init");
        auth.setSessionId("s1");
        auth.setUser(user);

        assertEquals("Init", auth.getMessage());
        assertEquals("s1", auth.getSessionId());
        assertSame(user, auth.getUser());

        // modifier
        UserResponse newUser = new UserResponse();
        auth.setMessage("Modifié");
        auth.setSessionId("s2");
        auth.setUser(newUser);

        assertEquals("Modifié", auth.getMessage());
        assertEquals("s2", auth.getSessionId());
        assertSame(newUser, auth.getUser());
    }

    @Test
    void equalsAndHashCode_sameValues_areEqual() {
        UserResponse user = new UserResponse();

        AuthResponse a1 = AuthResponse.builder()
                .message("m")
                .sessionId("sid")
                .user(user)
                .build();

        AuthResponse a2 = AuthResponse.builder()
                .message("m")
                .sessionId("sid")
                .user(user)
                .build();

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void toString_containsImportantFields() {
        AuthResponse auth = AuthResponse.builder()
                .message("OK")
                .sessionId("sess-xyz")
                .build();

        String s = auth.toString();
        assertTrue(s.contains("OK"));
        assertTrue(s.contains("sess-xyz"));
    }
}
