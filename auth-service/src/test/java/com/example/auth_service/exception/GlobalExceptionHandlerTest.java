package com.example.auth_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundException_deberiaRetornarNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/auth/99");

        ResponseEntity<ApiErrorResponse> response = handler.handleResourceNotFoundException(
                new ResourceNotFoundException("No existe un usuario auth con ID: 99"),
                request
        );

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Recurso no encontrado", response.getBody().getError());
        assertEquals("/auth/99", response.getBody().getPath());
    }

    @Test
    void handleBadRequestException_deberiaRetornarBadRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/auth/login");

        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequestException(
                new BadRequestException("Credenciales inválidas"),
                request
        );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Solicitud incorrecta", response.getBody().getError());
        assertEquals("/auth/login", response.getBody().getPath());
    }

    @Test
    void handleGeneralException_deberiaRetornarInternalServerError() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/auth");

        ResponseEntity<ApiErrorResponse> response = handler.handleGeneralException(
                new RuntimeException("Error inesperado"),
                request
        );

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Error interno del servidor", response.getBody().getError());
        assertEquals("/auth", response.getBody().getPath());
    }
}