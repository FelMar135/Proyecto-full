package com.example.resena_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.resena_service.exception.ResourceNotFoundException;
import com.example.resena_service.model.Resena;
import com.example.resena_service.dto.ResenaDTO;
import com.example.resena_service.repository.ResenaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

public class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    private ResenaService resenaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        resenaService = new ResenaService(
                resenaRepository,
                webClientBuilder);
    }

    @Test
    void buscarResenaPorId() {

        Resena resena = new Resena(
                1L,
                1L,
                101L,
                "Excelente gráfica",
                5,
                LocalDate.now());

        when(resenaRepository.findById(1L))
                .thenReturn(Optional.of(resena));

        ResenaDTO resultado =
                resenaService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarResenaNoExiste() {

        when(resenaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class, // O RuntimeException si no usas ResourceNotFoundException aquí
                () -> resenaService.findById(99L));
    }

    @Test
    void eliminarResena() {

        when(resenaRepository.existsById(1L))
                .thenReturn(true);

        // A diferencia de boleta que retorna un boolean, deleteById en spring suele ser void.
        // Lo ejecutamos y luego verificamos que se llamó al repositorio.
        resenaService.deleteById(1L);

        verify(resenaRepository)
                .deleteById(1L);
    }

    @Test
    void eliminarResenaNoExiste() {

        when(resenaRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> resenaService.deleteById(99L));
    }

    // Adaptamos el método final para que sea equivalente al 'totalCompradoPorUsuario' de Boleta, 
    // pero aplicado al contexto de Reseñas (buscar reseñas de un usuario).
    @Test
    void buscarResenasPorUsuarioId() {

        Resena resena = new Resena(
                1L,
                1L,
                101L,
                "Excelente gráfica",
                5,
                LocalDate.now());

        when(resenaRepository.findByUsuarioId(1L))
                .thenReturn(Arrays.asList(resena));

        List<ResenaDTO> resultados =
                resenaService.findByUsuarioId(1L);

        assertFalse(resultados.isEmpty());
        assertEquals(1L, resultados.get(0).getUsuarioId());
    }
}