// java
package com.example.StockFlow.dto.request;

import com.example.StockFlow.entity.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void builderAndGetters_shouldReturnProvidedValues() {
        RegisterRequest req = RegisterRequest.builder()
                .username("jean")
                .email("jean@example.com")
                .password("secret")
                .role(Role.CLIENT)
                .addressse("Rue 1")
                .tel("0123456789")
                .build();

        assertEquals("jean", req.getUsername());
        assertEquals("jean@example.com", req.getEmail());
        assertEquals("secret", req.getPassword());
        assertEquals(Role.CLIENT, req.getRole());
        assertEquals("Rue 1", req.getAddressse());
        assertEquals("0123456789", req.getTel());
    }

    @Test
    void setters_shouldModifyValues() {
        RegisterRequest req = RegisterRequest.builder()
                .username("a")
                .email("a@a.com")
                .password("p")
                .role(Role.CLIENT)
                .addressse("x")
                .tel("0")
                .build();

        req.setUsername("paul");
        req.setEmail("paul@example.com");
        req.setPassword("newpass");
        req.setRole(Role.ADMIN);
        req.setAddressse("Av. Test");
        req.setTel("0987654321");

        assertEquals("paul", req.getUsername());
        assertEquals("paul@example.com", req.getEmail());
        assertEquals("newpass", req.getPassword());
        assertEquals(Role.ADMIN, req.getRole());
        assertEquals("Av. Test", req.getAddressse());
        assertEquals("0987654321", req.getTel());
    }

    @Test
    void equalsAndHashCode_sameValues_areEqual() {
        RegisterRequest r1 = RegisterRequest.builder()
                .username("luc")
                .email("luc@example.com")
                .password("pwd")
                .role(Role.CLIENT)
                .addressse("Adr")
                .tel("111")
                .build();

        RegisterRequest r2 = RegisterRequest.builder()
                .username("luc")
                .email("luc@example.com")
                .password("pwd")
                .role(Role.CLIENT)
                .addressse("Adr")
                .tel("111")
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toString_containsImportantFields() {
        RegisterRequest req = RegisterRequest.builder()
                .username("marie")
                .email("marie@example.com")
                .build();

        String s = req.toString();
        assertTrue(s.contains("marie"));
        assertTrue(s.contains("marie@example.com"));
    }
}
