package com.example.pago_service.service;

import com.example.pago_service.dto.PagoDTO;
import com.example.pago_service.exception.BadRequestException;
import com.example.pago_service.exception.ResourceNotFoundException;
import com.example.pago_service.model.Pago;
import com.example.pago_service.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    private PagoService pagoService;

    @BeforeEach
    void setUp() {
        WebClient ordenWebClient = crearWebClientConRespuesta("true");
        pagoService = new PagoService(pagoRepository, ordenWebClient);
    }

    @Test
    void obtenerTodos_deberiaRetornarListaDePagos() {
        when(pagoRepository.findAll()).thenReturn(List.of(crearPago()));

        List<PagoDTO> resultado = pagoService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(1L, resultado.get(0).getOrdenId());
        assertEquals("PAGADO", resultado.get(0).getEstado());

        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornarPago() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(crearPago()));

        PagoDTO resultado = pagoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("DEBITO", resultado.getMetodoPago());

        verify(pagoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> pagoService.obtenerPorId(99L)
        );

        assertEquals("No existe un pago con ID: 99", exception.getMessage());
    }

    @Test
    void existePorId_deberiaRetornarTrue() {
        when(pagoRepository.existsById(1L)).thenReturn(true);

        boolean resultado = pagoService.existePorId(1L);

        assertTrue(resultado);
        verify(pagoRepository, times(1)).existsById(1L);
    }

    @Test
    void crear_conDatosValidos_deberiaGuardarPago() {
        PagoDTO dto = crearPagoDTO();
        dto.setId(null);
        dto.setFechaPago(null);

        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            pago.setId(1L);
            return pago;
        });

        PagoDTO resultado = pagoService.crear(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(1L, resultado.getOrdenId());
        assertEquals(new BigDecimal("399990"), resultado.getMonto());
        assertNotNull(resultado.getFechaPago());

        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void crear_cuandoOrdenNoExiste_deberiaLanzarBadRequestException() {
        WebClient webClientFalse = crearWebClientConRespuesta("false");
        PagoService serviceConOrdenInexistente = new PagoService(pagoRepository, webClientFalse);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> serviceConOrdenInexistente.crear(crearPagoDTO())
        );

        assertEquals("La orden con ID 1 no existe", exception.getMessage());
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void actualizar_cuandoExiste_deberiaActualizarPago() {
        Pago existente = crearPago();

        PagoDTO actualizado = crearPagoDTO();
        actualizado.setMonto(new BigDecimal("899990"));
        actualizado.setMetodoPago("CREDITO");
        actualizado.setEstado("PENDIENTE");

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PagoDTO resultado = pagoService.actualizar(1L, actualizado);

        assertEquals(new BigDecimal("899990"), resultado.getMonto());
        assertEquals("CREDITO", resultado.getMetodoPago());
        assertEquals("PENDIENTE", resultado.getEstado());

        verify(pagoRepository, times(1)).findById(1L);
        verify(pagoRepository, times(1)).save(existente);
    }

    @Test
    void actualizar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> pagoService.actualizar(99L, crearPagoDTO())
        );

        assertEquals("No existe un pago con ID: 99", exception.getMessage());
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminarPago() {
        when(pagoRepository.existsById(1L)).thenReturn(true);

        pagoService.eliminar(1L);

        verify(pagoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(pagoRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> pagoService.eliminar(99L)
        );

        assertEquals("No existe un pago con ID: 99", exception.getMessage());
        verify(pagoRepository, never()).deleteById(anyLong());
    }

    @Test
    void obtenerPorOrdenId_deberiaRetornarPagos() {
        when(pagoRepository.findByOrdenId(1L)).thenReturn(List.of(crearPago()));

        List<PagoDTO> resultado = pagoService.obtenerPorOrdenId(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getOrdenId());

        verify(pagoRepository, times(1)).findByOrdenId(1L);
    }

    @Test
    void calcularIva_conMontoValido_deberiaRetornarIva() {
        Double resultado = pagoService.calcularIva(100000.0);

        assertEquals(19000.0, resultado);
    }

    @Test
    void calcularIva_conMontoInvalido_deberiaLanzarBadRequestException() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> pagoService.calcularIva(0.0)
        );

        assertEquals("El monto debe ser mayor a 0 para calcular IVA", exception.getMessage());
    }

    private WebClient crearWebClientConRespuesta(String respuesta) {
        return WebClient.builder()
                .exchangeFunction(request -> Mono.just(
                        ClientResponse.create(HttpStatus.OK)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(respuesta)
                                .build()
                ))
                .build();
    }

    private Pago crearPago() {
        return Pago.builder()
                .id(1L)
                .ordenId(1L)
                .monto(new BigDecimal("399990"))
                .metodoPago("DEBITO")
                .estado("PAGADO")
                .fechaPago(LocalDateTime.now())
                .build();
    }

    private PagoDTO crearPagoDTO() {
        return PagoDTO.builder()
                .id(1L)
                .ordenId(1L)
                .monto(new BigDecimal("399990"))
                .metodoPago("DEBITO")
                .estado("PAGADO")
                .fechaPago(LocalDateTime.now())
                .build();
    }
}