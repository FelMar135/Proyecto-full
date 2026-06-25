package com.example.envio_service.service;

import com.example.envio_service.dto.EnvioDTO;
import com.example.envio_service.exception.BadRequestException;
import com.example.envio_service.exception.ResourceNotFoundException;
import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    private EnvioService envioService;

    @BeforeEach
    void setUp() {
        WebClient ordenWebClient = crearWebClientConRespuesta("true");
        envioService = new EnvioService(envioRepository, ordenWebClient);
    }

    @Test
    void obtenerTodos_deberiaRetornarListaDeEnvios() {
        when(envioRepository.findAll()).thenReturn(List.of(crearEnvio()));

        List<EnvioDTO> resultado = envioService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(1L, resultado.get(0).getOrdenId());
        assertEquals("Santiago", resultado.get(0).getCiudad());

        verify(envioRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornarEnvio() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(crearEnvio()));

        EnvioDTO resultado = envioService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Providencia", resultado.getComuna());

        verify(envioRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> envioService.obtenerPorId(99L)
        );

        assertEquals("No existe un envío con ID: 99", exception.getMessage());
    }

    @Test
    void existePorId_deberiaRetornarTrue() {
        when(envioRepository.existsById(1L)).thenReturn(true);

        boolean resultado = envioService.existePorId(1L);

        assertTrue(resultado);
        verify(envioRepository, times(1)).existsById(1L);
    }

    @Test
    void crear_conDatosValidos_deberiaGuardarEnvio() {
        EnvioDTO dto = crearEnvioDTO();
        dto.setId(null);
        dto.setNumeroSeguimiento(null);
        dto.setFechaEnvio(null);
        dto.setFechaEntregaEstimada(null);

        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> {
            Envio envio = invocation.getArgument(0);
            envio.setId(1L);
            return envio;
        });

        EnvioDTO resultado = envioService.crear(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertNotNull(resultado.getNumeroSeguimiento());
        assertTrue(resultado.getNumeroSeguimiento().startsWith("ENV-"));
        assertNotNull(resultado.getFechaEnvio());
        assertNotNull(resultado.getFechaEntregaEstimada());

        verify(envioRepository, times(1)).save(any(Envio.class));
    }

    @Test
    void crear_cuandoOrdenNoExiste_deberiaLanzarBadRequestException() {
        WebClient webClientFalse = crearWebClientConRespuesta("false");
        EnvioService serviceConOrdenInexistente = new EnvioService(envioRepository, webClientFalse);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> serviceConOrdenInexistente.crear(crearEnvioDTO())
        );

        assertEquals("La orden con ID 1 no existe", exception.getMessage());
        verify(envioRepository, never()).save(any());
    }

    @Test
    void actualizar_cuandoExiste_deberiaActualizarEnvio() {
        Envio existente = crearEnvio();

        EnvioDTO actualizado = crearEnvioDTO();
        actualizado.setDireccionEnvio("Nueva dirección 123");
        actualizado.setEstado("EN_TRANSITO");

        when(envioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EnvioDTO resultado = envioService.actualizar(1L, actualizado);

        assertEquals("Nueva dirección 123", resultado.getDireccionEnvio());
        assertEquals("EN_TRANSITO", resultado.getEstado());

        verify(envioRepository, times(1)).findById(1L);
        verify(envioRepository, times(1)).save(existente);
    }

    @Test
    void actualizar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> envioService.actualizar(99L, crearEnvioDTO())
        );

        assertEquals("No existe un envío con ID: 99", exception.getMessage());
        verify(envioRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminarEnvio() {
        when(envioRepository.existsById(1L)).thenReturn(true);

        envioService.eliminar(1L);

        verify(envioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(envioRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> envioService.eliminar(99L)
        );

        assertEquals("No existe un envío con ID: 99", exception.getMessage());
        verify(envioRepository, never()).deleteById(anyLong());
    }

    @Test
    void obtenerPorOrdenId_deberiaRetornarEnvios() {
        when(envioRepository.findByOrdenId(1L)).thenReturn(List.of(crearEnvio()));

        List<EnvioDTO> resultado = envioService.obtenerPorOrdenId(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getOrdenId());

        verify(envioRepository, times(1)).findByOrdenId(1L);
    }

    @Test
    void obtenerPorEstado_deberiaRetornarEnvios() {
        when(envioRepository.findByEstado("EN_PREPARACION")).thenReturn(List.of(crearEnvio()));

        List<EnvioDTO> resultado = envioService.obtenerPorEstado("EN_PREPARACION");

        assertEquals(1, resultado.size());
        assertEquals("EN_PREPARACION", resultado.get(0).getEstado());

        verify(envioRepository, times(1)).findByEstado("EN_PREPARACION");
    }

    @Test
    void obtenerPorCiudad_deberiaRetornarEnvios() {
        when(envioRepository.findByCiudad("Santiago")).thenReturn(List.of(crearEnvio()));

        List<EnvioDTO> resultado = envioService.obtenerPorCiudad("Santiago");

        assertEquals(1, resultado.size());
        assertEquals("Santiago", resultado.get(0).getCiudad());

        verify(envioRepository, times(1)).findByCiudad("Santiago");
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

    private Envio crearEnvio() {
        return Envio.builder()
                .id(1L)
                .ordenId(1L)
                .direccionEnvio("Av. Providencia 1234")
                .comuna("Providencia")
                .ciudad("Santiago")
                .empresaTransportista("Chilexpress")
                .numeroSeguimiento("ENV-1001")
                .estado("EN_PREPARACION")
                .fechaEnvio(LocalDateTime.now())
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(3))
                .build();
    }

    private EnvioDTO crearEnvioDTO() {
        return EnvioDTO.builder()
                .id(1L)
                .ordenId(1L)
                .direccionEnvio("Av. Providencia 1234")
                .comuna("Providencia")
                .ciudad("Santiago")
                .empresaTransportista("Chilexpress")
                .numeroSeguimiento("ENV-1001")
                .estado("EN_PREPARACION")
                .fechaEnvio(LocalDateTime.now())
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(3))
                .build();
    }
}