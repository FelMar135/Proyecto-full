package com.example.envio_service.service;

import com.example.envio_service.dto.EnvioDTO;
import com.example.envio_service.exception.ResourceNotFoundException;
import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private WebClient ordenWebClient;

    @InjectMocks
    private EnvioService envioService;

    @Test
    void obtenerTodos_deberiaRetornarListaDeEnvios() {
        Envio envio = Envio.builder()
                .id(1L)
                .ordenId(10L)
                .direccionEnvio("Av. Providencia 1234")
                .comuna("Providencia")
                .ciudad("Santiago")
                .empresaTransportista("Chilexpress")
                .numeroSeguimiento("ENV-1001")
                .estado("EN_TRANSITO")
                .fechaEnvio(LocalDateTime.now())
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(3))
                .build();

        when(envioRepository.findAll()).thenReturn(List.of(envio));

        List<EnvioDTO> resultado = envioService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(10L, resultado.get(0).getOrdenId());
        assertEquals("Santiago", resultado.get(0).getCiudad());
        assertEquals("EN_TRANSITO", resultado.get(0).getEstado());

        verify(envioRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornarEnvio() {
        Envio envio = Envio.builder()
                .id(1L)
                .ordenId(10L)
                .direccionEnvio("Los Leones 550")
                .comuna("Ñuñoa")
                .ciudad("Santiago")
                .empresaTransportista("Starken")
                .numeroSeguimiento("ENV-2002")
                .estado("ENTREGADO")
                .fechaEnvio(LocalDateTime.now())
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(2))
                .build();

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        EnvioDTO resultado = envioService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getOrdenId());
        assertEquals("Ñuñoa", resultado.getComuna());
        assertEquals("ENTREGADO", resultado.getEstado());

        verify(envioRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> envioService.obtenerPorId(99L));

        verify(envioRepository, times(1)).findById(99L);
    }

    @Test
    void obtenerPorEstado_deberiaRetornarEnviosFiltrados() {
        Envio envio = Envio.builder()
                .id(1L)
                .ordenId(11L)
                .direccionEnvio("Av. Grecia 750")
                .comuna("Ñuñoa")
                .ciudad("Santiago")
                .empresaTransportista("Blue Express")
                .numeroSeguimiento("ENV-3003")
                .estado("EN_PREPARACION")
                .fechaEnvio(LocalDateTime.now())
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(4))
                .build();

        when(envioRepository.findByEstado("EN_PREPARACION")).thenReturn(List.of(envio));

        List<EnvioDTO> resultado = envioService.obtenerPorEstado("EN_PREPARACION");

        assertEquals(1, resultado.size());
        assertEquals("EN_PREPARACION", resultado.get(0).getEstado());

        verify(envioRepository, times(1)).findByEstado("EN_PREPARACION");
    }

    @Test
    void obtenerPorCiudad_deberiaRetornarEnviosFiltrados() {
        Envio envio = Envio.builder()
                .id(1L)
                .ordenId(12L)
                .direccionEnvio("Camino El Alba 890")
                .comuna("Las Condes")
                .ciudad("Santiago")
                .empresaTransportista("Chilexpress")
                .numeroSeguimiento("ENV-4004")
                .estado("EN_TRANSITO")
                .fechaEnvio(LocalDateTime.now())
                .fechaEntregaEstimada(LocalDateTime.now().plusDays(3))
                .build();

        when(envioRepository.findByCiudad("Santiago")).thenReturn(List.of(envio));

        List<EnvioDTO> resultado = envioService.obtenerPorCiudad("Santiago");

        assertEquals(1, resultado.size());
        assertEquals("Santiago", resultado.get(0).getCiudad());

        verify(envioRepository, times(1)).findByCiudad("Santiago");
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminarEnvio() {
        when(envioRepository.existsById(1L)).thenReturn(true);

        envioService.eliminar(1L);

        verify(envioRepository, times(1)).existsById(1L);
        verify(envioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(envioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> envioService.eliminar(99L));

        verify(envioRepository, times(1)).existsById(99L);
        verify(envioRepository, never()).deleteById(99L);
    }
}