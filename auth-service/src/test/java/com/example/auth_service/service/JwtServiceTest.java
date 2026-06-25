package com.example.auth_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateToken_deberiaGenerarTokenValido() {
        String token = jwtService.generateToken("admin@gpustore.cl");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtService.isValid(token));
    }

    @Test
    void getEmailFromToken_conTokenValido_deberiaRetornarEmail() {
        String token = jwtService.generateToken("admin@gpustore.cl");

        String email = jwtService.getEmailFromToken(token);

        assertEquals("admin@gpustore.cl", email);
    }

    @Test
    void getEmailFromToken_conBearerToken_deberiaRetornarEmail() {
        String token = jwtService.generateToken("admin@gpustore.cl");

        String email = jwtService.getEmailFromToken("Bearer " + token);

        assertEquals("admin@gpustore.cl", email);
    }

    @Test
    void getEmailFromToken_conTokenNulo_deberiaRetornarNull() {
        assertNull(jwtService.getEmailFromToken(null));
    }

    @Test
    void getEmailFromToken_conTokenInvalido_deberiaRetornarNull() {
        assertNull(jwtService.getEmailFromToken("token.malo"));
    }

    @Test
    void isValid_conTokenNulo_deberiaRetornarFalse() {
        assertFalse(jwtService.isValid(null));
    }

    @Test
    void isValid_conTokenInvalido_deberiaRetornarFalse() {
        assertFalse(jwtService.isValid("token.malo"));
    }
}