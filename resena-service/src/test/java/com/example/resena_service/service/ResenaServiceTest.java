package com.example.resena_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.example.resena_service.exception.ResourceNotFoundException;
import com.example.resena_service.model.Resena;
import com.example.resena_service.dto.ResenaDTO;
import com.example.resena_service.repository.ResenaRepository;
import com.github.javafaker.Faker;

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
    
    // 1. Declaramos Faker y nuestra reseña global de prueba
    private Faker faker;
    private Resena resenaPrueba;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Inicializamos Faker en español
        faker = new Faker(new Locale("es"));

        resenaService = new ResenaService(
                resenaRepository,
                webClientBuilder);

        // 2. Creamos la reseña con datos aleatorios de Faker
        resenaPrueba = new Resena(
                1L, // Mantenemos el ID 1L fijo para que los mocks sean más fáciles de leer
                faker.number().numberBetween(1L, 100L), // usuarioId aleatorio
                faker.number().numberBetween(100L, 500L), // gpuId aleatorio
                faker.lorem().sentence(8), // Comentario aleatorio de 8 palabras
                faker.number().numberBetween(1, 6), // Calificación aleatoria entre 1 y 5
                LocalDate.now()
        );
    }

    @Test
    void buscarResenaPorId() {

        // Usamos la resenaPrueba generada por Faker
        when(resenaRepository.findById(1L))
                .thenReturn(Optional.of(resenaPrueba));

        ResenaDTO resultado =
                resenaService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        
        // Imprimimos en consola para ver el comentario falso generado
        System.out.println("Comentario generado por Faker: " + resenaPrueba.getComentario());
        System.out.println("Calificación: " + resenaPrueba.getCalificacion() + " estrellas");
    }

    @Test
    void buscarResenaNoExiste() {

        when(resenaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class, // O RuntimeException según tu implementación
                () -> resenaService.findById(99L));
    }

    @Test
    void eliminarResena() {

        when(resenaRepository.existsById(1L))
                .thenReturn(true);

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

    @Test
    void buscarResenasPorUsuarioId() {

        // Extraemos el usuarioId dinámico que Faker inventó
        Long usuarioFalsoId = resenaPrueba.getUsuarioId();

        when(resenaRepository.findByUsuarioId(usuarioFalsoId))
                .thenReturn(Arrays.asList(resenaPrueba));

        List<ResenaDTO> resultados =
                resenaService.findByUsuarioId(usuarioFalsoId);

        assertFalse(resultados.isEmpty());
        // Verificamos que el usuario de la respuesta coincida con el de Faker
        assertEquals(usuarioFalsoId, resultados.get(0).getUsuarioId());
    }
}