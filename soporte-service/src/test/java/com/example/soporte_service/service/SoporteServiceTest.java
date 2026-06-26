package com.example.soporte_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock private SoporteRepository soporteRepository;
    @Mock private WebClient.Builder webClientBuilder;

    // --- MOCKS PARA WEBCLIENT ---
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private SoporteService soporteService;
    private Faker faker;
    private Soporte soportePrueba;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        faker = new Faker(new Locale("es"));

        soporteService = new SoporteService(soporteRepository, webClientBuilder);

        soportePrueba = new Soporte(
                1L,
                faker.number().numberBetween(1L, 100L),
                faker.number().numberBetween(200L, 500L),
                faker.lorem().sentence(4),
                faker.lorem().paragraph(),
                "ABIERTO",
                LocalDate.now()
        );
    }

    @Test
    void buscarTodosLosSoportes() {
        when(soporteRepository.findAll()).thenReturn(Arrays.asList(soportePrueba));
        List<SoporteDTO> resultados = soporteService.findAll();
        assertFalse(resultados.isEmpty());
    }

    @Test
    void buscarSoportePorId() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soportePrueba));
        SoporteDTO resultado = soporteService.findById(1L);
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarSoporteNoExiste() {
        when(soporteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> soporteService.findById(99L));
    }

    @Test
    void crearSoporteExitoso() {
        // Simulamos WebClient devolviendo true
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(reactor.core.publisher.Mono.just(true));

        when(soporteRepository.save(any(Soporte.class))).thenReturn(soportePrueba);

        SoporteDTO dto = SoporteDTO.fromModel(soportePrueba);
        SoporteDTO resultado = soporteService.save(dto);

        assertNotNull(resultado);
        verify(soporteRepository).save(any(Soporte.class));
    }

    @Test
    void actualizarSoporteExitoso() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soportePrueba));
        when(soporteRepository.save(any(Soporte.class))).thenReturn(soportePrueba);

        SoporteDTO dtoAActualizar = SoporteDTO.fromModel(soportePrueba);
        dtoAActualizar.setAsunto("Asunto Modificado");

        SoporteDTO resultado = soporteService.update(1L, dtoAActualizar);

        assertNotNull(resultado);
        verify(soporteRepository).save(any(Soporte.class));
    }

    @Test
    void eliminarSoporte() {
        when(soporteRepository.existsById(1L)).thenReturn(true);
        soporteService.deleteById(1L);
        verify(soporteRepository).deleteById(1L);
    }

    @Test
    void eliminarSoporteNoExiste() {
        when(soporteRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> soporteService.deleteById(99L));
    }

    @Test
    void buscarSoportesPorUsuarioId() {
        when(soporteRepository.findByUsuarioId(soportePrueba.getUsuarioId())).thenReturn(Arrays.asList(soportePrueba));
        List<SoporteDTO> resultados = soporteService.findByUsuarioId(soportePrueba.getUsuarioId());
        assertFalse(resultados.isEmpty());
    }

    @Test
    void buscarSoportesPorOrdenId() {
        when(soporteRepository.findByOrdenId(soportePrueba.getOrdenId())).thenReturn(Arrays.asList(soportePrueba));
        List<SoporteDTO> resultados = soporteService.findByOrdenId(soportePrueba.getOrdenId());
        assertFalse(resultados.isEmpty());
    }

    @Test
    void crearSoporteFallaPorUsuarioOOrdenNoExiste() {
        // Simulamos el WebClient devolviendo FALSE (como si el usuario no existiera)
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
        // Retornamos FALSE
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(reactor.core.publisher.Mono.just(false));

        SoporteDTO dtoAEnviar = SoporteDTO.fromModel(soportePrueba);
        
        // Verificamos que al intentar guardar con datos falsos, explote con un BadRequestException
        assertThrows(com.example.soporte_service.exception.BadRequestException.class, 
            () -> soporteService.save(dtoAEnviar)
        );
        
        // Verificamos que NUNCA haya intentado guardar en la base de datos
        verify(soporteRepository, never()).save(any(com.example.soporte_service.model.Soporte.class));
    }
}