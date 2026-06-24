package com.example.soporte_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.example.soporte_service.exception.ResourceNotFoundException;
import com.example.soporte_service.model.Soporte;
import com.example.soporte_service.dto.SoporteDTO;
import com.example.soporte_service.repository.SoporteRepository;
import com.github.javafaker.Faker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

public class SoporteServiceTest {

    @Mock
    private SoporteRepository soporteRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    private SoporteService soporteService;
    
    // Declaramos Faker y un objeto de prueba global
    private Faker faker;
    private Soporte soportePrueba;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Inicializamos Faker en español
        faker = new Faker(new Locale("es"));

        soporteService = new SoporteService(
                soporteRepository,
                webClientBuilder);

        // Creamos una orden de prueba usando Faker para todos los datos (excepto el ID principal)
        soportePrueba = new Soporte(
                1L,
                faker.number().numberBetween(1L, 100L), // usuarioId aleatorio entre 1 y 100
                faker.number().numberBetween(200L, 500L), // ordenId aleatorio
                faker.lorem().sentence(4), // Asunto: Una frase de 4 palabras
                faker.lorem().paragraph(), // Descripción: Un párrafo completo
                faker.options().option("ABIERTO", "EN_PROCESO", "CERRADO"), // Estado aleatorio
                LocalDate.now() // Fecha actual
        );
    }

    @Test
    void buscarSoportePorId() {

        when(soporteRepository.findById(1L))
                .thenReturn(Optional.of(soportePrueba));

        SoporteDTO resultado = soporteService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        
        // Podemos imprimir en consola para ver la magia de Faker
        System.out.println("Soporte generado por Faker: " + soportePrueba.getAsunto());
    }

    @Test
    void buscarSoporteNoExiste() {

        when(soporteRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> soporteService.findById(99L));
    }

    @Test
    void eliminarSoporte() {

        when(soporteRepository.existsById(1L))
                .thenReturn(true);

        soporteService.deleteById(1L);

        verify(soporteRepository).deleteById(1L);
    }

    @Test
    void eliminarSoporteNoExiste() {

        when(soporteRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> soporteService.deleteById(99L));
    }

    @Test
    void buscarSoportesPorUsuarioId() {

        when(soporteRepository.findByUsuarioId(soportePrueba.getUsuarioId()))
                .thenReturn(Arrays.asList(soportePrueba));

        List<SoporteDTO> resultados =
                soporteService.findByUsuarioId(soportePrueba.getUsuarioId());

        assertFalse(resultados.isEmpty());
        assertEquals(soportePrueba.getUsuarioId(), resultados.get(0).getUsuarioId());
    }

    @Test
    void buscarSoportesPorOrdenId() {

        when(soporteRepository.findByOrdenId(soportePrueba.getOrdenId()))
                .thenReturn(Arrays.asList(soportePrueba));

        List<SoporteDTO> resultados =
                soporteService.findByOrdenId(soportePrueba.getOrdenId());

        assertFalse(resultados.isEmpty());
        assertEquals(soportePrueba.getOrdenId(), resultados.get(0).getOrdenId());
    }
}