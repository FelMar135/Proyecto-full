package com.example.pago_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundException_deberiaRetornarNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/pagos/99");

        ResponseEntity<ApiErrorResponse> response = handler.handleResourceNotFoundException(
                new ResourceNotFoundException("No existe un pago con ID: 99"),
                request
        );

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Recurso no encontrado", response.getBody().getError());
        assertEquals("/pagos/99", response.getBody().getPath());
    }

    @Test
    void handleBadRequestException_deberiaRetornarBadRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/pagos");

        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequestException(
                new BadRequestException("La orden con ID 99 no existe"),
                request
        );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Solicitud incorrecta", response.getBody().getError());
        assertEquals("/pagos", response.getBody().getPath());
    }

    @Test
    void handleGeneralException_deberiaRetornarInternalServerError() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/pagos");

        ResponseEntity<ApiErrorResponse> response = handler.handleGeneralException(
                new RuntimeException("Error inesperado"),
                request
        );

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Error interno del servidor", response.getBody().getError());
        assertEquals("/pagos", response.getBody().getPath());
    }
}