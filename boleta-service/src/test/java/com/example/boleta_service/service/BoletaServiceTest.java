package com.example.boleta_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import com.example.boleta_service.exception.ResourceNotFoundException;
import com.example.boleta_service.model.Boleta;
import com.example.boleta_service.repository.BoletaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

public class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    private BoletaService boletaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        boletaService = new BoletaService(
                boletaRepository,
                webClientBuilder);
    }

    @Test
    void buscarBoletaPorId() {

        Boleta boleta = new Boleta(
                1L,
                1L,
                1L,
                100000.0,
                19000.0,
                119000.0,
                LocalDate.now(),
                "BOL-1001");

        when(boletaRepository.findById(1L))
                .thenReturn(Optional.of(boleta));

        Boleta resultado =
                boletaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarBoletaNoExiste() {

        when(boletaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> boletaService.buscarPorId(99L));
    }

    @Test
    void eliminarBoleta() {

        when(boletaRepository.existsById(1L))
                .thenReturn(true);

        boolean resultado =
                boletaService.eliminar(1L);

        assertTrue(resultado);

        verify(boletaRepository)
                .deleteById(1L);
    }

    @Test
    void eliminarBoletaNoExiste() {

        when(boletaRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> boletaService.eliminar(99L));
    }

    @Test
    void totalCompradoPorUsuario() {

        when(boletaRepository.totalCompradoPorUsuario(1L))
                .thenReturn(357000.0);

        Double total =
                boletaService.totalCompradoPorUsuario(1L);

        assertEquals(357000.0, total);
    }
}