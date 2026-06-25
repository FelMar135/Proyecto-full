package com.example.auth_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashServiceTest {

    private final HashService hashService = new HashService();

    @Test
    void sha1_deberiaRetornarHashCorrecto() {
        String resultado = hashService.sha1("123456");

        assertEquals("7c4a8d09ca3762af61e59520943dc26494f8941b", resultado);
    }

    @Test
    void sha1_conTextoDistinto_deberiaRetornarHashDistinto() {
        String hash1 = hashService.sha1("123456");
        String hash2 = hashService.sha1("abcdef");

        assertNotEquals(hash1, hash2);
    }

    @Test
    void sha1_noDebeRetornarNullNiVacio() {
        String resultado = hashService.sha1("admin");

        assertNotNull(resultado);
        assertFalse(resultado.isBlank());
    }
}