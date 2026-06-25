package com.example.boleta_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadRequest_deberiaRetornarBadRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/boletas");

        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(
                new BadRequestException("El subtotal debe ser mayor a 0"),
                request
        );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("/boletas", response.getBody().getPath());
    }

    @Test
    void handleNotFound_deberiaRetornarNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/boletas/99");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(
                new ResourceNotFoundException("No se encontró la boleta con ID: 99"),
                request
        );

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("/boletas/99", response.getBody().getPath());
    }

    @Test
    void handleGeneralException_deberiaRetornarInternalServerError() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/boletas");

        ResponseEntity<ApiErrorResponse> response = handler.handleGeneralException(
                new RuntimeException("Error inesperado"),
                request
        );

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("/boletas", response.getBody().getPath());
    }
}