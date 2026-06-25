package com.example.boleta_service.service;

import com.example.boleta_service.exception.BadRequestException;
import com.example.boleta_service.exception.ResourceNotFoundException;
import com.example.boleta_service.model.Boleta;
import com.example.boleta_service.repository.BoletaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;

    private BoletaService boletaService;

    @BeforeEach
    void setUp() {
        WebClient.Builder webClientBuilder = WebClient.builder()
                .exchangeFunction(request -> Mono.just(
                        ClientResponse.create(HttpStatus.OK)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body("true")
                                .build()
                ));

        boletaService = new BoletaService(boletaRepository, webClientBuilder);

        ReflectionTestUtils.setField(boletaService, "usuarioServiceUrl", "http://localhost:9091");
        ReflectionTestUtils.setField(boletaService, "ordenServiceUrl", "http://localhost:9095");
    }

    @Test
    void guardar_conDatosValidos_deberiaGuardarBoleta() {
        Boleta boleta = crearBoleta();
        boleta.setId(null);
        boleta.setIva(null);
        boleta.setTotal(null);
        boleta.setFechaEmision(null);
        boleta.setNumeroBoleta(null);

        when(boletaRepository.save(any(Boleta.class))).thenAnswer(invocation -> {
            Boleta guardada = invocation.getArgument(0);
            guardada.setId(1L);
            return guardada;
        });

        Boleta resultado = boletaService.guardar(boleta);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(19000.0, resultado.getIva());
        assertEquals(119000.0, resultado.getTotal());
        assertNotNull(resultado.getFechaEmision());
        assertNotNull(resultado.getNumeroBoleta());
        assertTrue(resultado.getNumeroBoleta().startsWith("BOL-"));

        verify(boletaRepository, times(1)).save(any(Boleta.class));
    }

    @Test
    void guardar_conBoletaNula_deberiaLanzarBadRequestException() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> boletaService.guardar(null)
        );

        assertEquals("La boleta no puede ser nula", exception.getMessage());
        verify(boletaRepository, never()).save(any());
    }

    @Test
    void guardar_conSubtotalCero_deberiaLanzarBadRequestException() {
        Boleta boleta = crearBoleta();
        boleta.setSubtotal(0.0);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> boletaService.guardar(boleta)
        );

        assertEquals("El subtotal debe ser mayor a 0", exception.getMessage());
        verify(boletaRepository, never()).save(any());
    }

    @Test
    void listar_deberiaRetornarBoletas() {
        when(boletaRepository.findAll()).thenReturn(List.of(crearBoleta()));

        List<Boleta> resultado = boletaService.listar();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());

        verify(boletaRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarBoleta() {
        when(boletaRepository.findById(1L)).thenReturn(Optional.of(crearBoleta()));

        Boleta resultado = boletaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        verify(boletaRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(boletaRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> boletaService.buscarPorId(99L)
        );

        assertEquals("No se encontró la boleta con ID: 99", exception.getMessage());
    }

    @Test
    void existePorId_deberiaRetornarTrue() {
        when(boletaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = boletaService.existePorId(1L);

        assertTrue(resultado);
        verify(boletaRepository, times(1)).existsById(1L);
    }

    @Test
    void actualizar_cuandoExiste_deberiaActualizarBoleta() {
        Boleta existente = crearBoleta();

        Boleta actualizada = crearBoleta();
        actualizada.setOrdenId(2L);
        actualizada.setUsuarioId(2L);
        actualizada.setSubtotal(200000.0);

        when(boletaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Boleta resultado = boletaService.actualizar(1L, actualizada);

        assertEquals(2L, resultado.getOrdenId());
        assertEquals(2L, resultado.getUsuarioId());
        assertEquals(200000.0, resultado.getSubtotal());
        assertEquals(38000.0, resultado.getIva());
        assertEquals(238000.0, resultado.getTotal());

        verify(boletaRepository, times(1)).save(existente);
    }

    @Test
    void actualizar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(boletaRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> boletaService.actualizar(99L, crearBoleta())
        );

        assertEquals("No se encontró la boleta con ID: 99", exception.getMessage());
        verify(boletaRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminarBoleta() {
        when(boletaRepository.existsById(1L)).thenReturn(true);

        boletaService.eliminar(1L);

        verify(boletaRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(boletaRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> boletaService.eliminar(99L)
        );

        assertEquals("No se encontró la boleta con ID: 99", exception.getMessage());
        verify(boletaRepository, never()).deleteById(anyLong());
    }

    @Test
    void buscarPorUsuarioId_deberiaRetornarBoletas() {
        when(boletaRepository.findByUsuarioId(1L)).thenReturn(List.of(crearBoleta()));

        List<Boleta> resultado = boletaService.buscarPorUsuarioId(1L);

        assertEquals(1, resultado.size());
        verify(boletaRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void buscarPorUsuarioId_invalido_deberiaLanzarBadRequestException() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> boletaService.buscarPorUsuarioId(0L)
        );

        assertEquals("El ID del usuario debe ser mayor a 0", exception.getMessage());
    }

    @Test
    void buscarPorOrdenId_deberiaRetornarBoletas() {
        when(boletaRepository.findByOrdenId(1L)).thenReturn(List.of(crearBoleta()));

        List<Boleta> resultado = boletaService.buscarPorOrdenId(1L);

        assertEquals(1, resultado.size());
        verify(boletaRepository, times(1)).findByOrdenId(1L);
    }

    @Test
    void totalCompradoPorUsuario_deberiaRetornarTotal() {
        when(boletaRepository.totalCompradoPorUsuario(1L)).thenReturn(357000.0);

        Double resultado = boletaService.totalCompradoPorUsuario(1L);

        assertEquals(357000.0, resultado);
        verify(boletaRepository, times(1)).totalCompradoPorUsuario(1L);
    }

    @Test
    void totalCompradoPorUsuario_cuandoEsNull_deberiaRetornarCero() {
        when(boletaRepository.totalCompradoPorUsuario(1L)).thenReturn(null);

        Double resultado = boletaService.totalCompradoPorUsuario(1L);

        assertEquals(0.0, resultado);
    }

    private Boleta crearBoleta() {
        return new Boleta(
                1L,
                1L,
                1L,
                100000.0,
                19000.0,
                119000.0,
                LocalDate.now(),
                "BOL-1001"
        );
    }
}