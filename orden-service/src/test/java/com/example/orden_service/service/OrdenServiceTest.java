package com.example.orden_service.service;

import com.example.orden_service.exception.BadRequestException;
import com.example.orden_service.exception.ResourceNotFoundException;
import com.example.orden_service.model.Orden;
import com.example.orden_service.repository.OrdenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private OrdenService ordenService;

    @Test
    void listar_deberiaRetornarListaDeOrdenes() {
        Orden orden = new Orden(1L, 10L, 20L, 399990.0, "CREADA");

        when(ordenRepository.findAll()).thenReturn(List.of(orden));

        List<Orden> resultado = ordenService.listar();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(10L, resultado.get(0).getUsuarioId());
        assertEquals(20L, resultado.get(0).getCarritoId());
        assertEquals(399990.0, resultado.get(0).getTotal());
        assertEquals("CREADA", resultado.get(0).getEstado());

        verify(ordenRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarOrden() {
        Orden orden = new Orden(1L, 10L, 20L, 899990.0, "PAGADA");

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        Orden resultado = ordenService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getUsuarioId());
        assertEquals(20L, resultado.getCarritoId());
        assertEquals("PAGADA", resultado.getEstado());

        verify(ordenRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ordenService.buscarPorId(99L));

        verify(ordenRepository, times(1)).findById(99L);
    }

    @Test
    void existePorId_deberiaRetornarTrueSiExiste() {
        when(ordenRepository.existsById(1L)).thenReturn(true);

        boolean resultado = ordenService.existePorId(1L);

        assertTrue(resultado);

        verify(ordenRepository, times(1)).existsById(1L);
    }

    @Test
    void guardar_cuandoUsuarioYCarritoExisten_deberiaGuardarOrden() {
        configurarUrlServicios();
        configurarWebClientConRespuesta(true);

        Orden orden = new Orden(null, 10L, 20L, 399990.0, "CREADA");
        Orden ordenGuardada = new Orden(1L, 10L, 20L, 399990.0, "CREADA");

        when(ordenRepository.save(any(Orden.class))).thenReturn(ordenGuardada);

        Orden resultado = ordenService.guardar(orden);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getUsuarioId());
        assertEquals(20L, resultado.getCarritoId());
        assertEquals(399990.0, resultado.getTotal());
        assertEquals("CREADA", resultado.getEstado());

        verify(ordenRepository, times(1)).save(any(Orden.class));
    }

    @Test
    void guardar_cuandoTotalEsCero_deberiaLanzarBadRequestException() {
        Orden orden = new Orden(null, 10L, 20L, 0.0, "CREADA");

        assertThrows(BadRequestException.class, () -> ordenService.guardar(orden));

        verify(ordenRepository, never()).save(any(Orden.class));
    }

    @Test
    void guardar_cuandoUsuarioNoExiste_deberiaLanzarBadRequestException() {
        configurarUrlServicios();
        configurarWebClientConRespuesta(false);

        Orden orden = new Orden(null, 10L, 20L, 399990.0, "CREADA");

        assertThrows(BadRequestException.class, () -> ordenService.guardar(orden));

        verify(ordenRepository, never()).save(any(Orden.class));
    }

    @Test
    void actualizar_cuandoExiste_deberiaActualizarOrden() {
        configurarUrlServicios();
        configurarWebClientConRespuesta(true);

        Orden ordenExistente = new Orden(1L, 10L, 20L, 399990.0, "CREADA");
        Orden ordenNueva = new Orden(null, 11L, 21L, 499990.0, "PAGADA");
        Orden ordenActualizada = new Orden(1L, 11L, 21L, 499990.0, "PAGADA");

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenExistente));
        when(ordenRepository.save(any(Orden.class))).thenReturn(ordenActualizada);

        Orden resultado = ordenService.actualizar(1L, ordenNueva);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(11L, resultado.getUsuarioId());
        assertEquals(21L, resultado.getCarritoId());
        assertEquals(499990.0, resultado.getTotal());
        assertEquals("PAGADA", resultado.getEstado());

        verify(ordenRepository, times(1)).findById(1L);
        verify(ordenRepository, times(1)).save(any(Orden.class));
    }

    @Test
    void actualizar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        Orden ordenNueva = new Orden(null, 11L, 21L, 499990.0, "PAGADA");

        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ordenService.actualizar(99L, ordenNueva));

        verify(ordenRepository, times(1)).findById(99L);
        verify(ordenRepository, never()).save(any(Orden.class));
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminarOrden() {
        when(ordenRepository.existsById(1L)).thenReturn(true);

        ordenService.eliminar(1L);

        verify(ordenRepository, times(1)).existsById(1L);
        verify(ordenRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(ordenRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> ordenService.eliminar(99L));

        verify(ordenRepository, times(1)).existsById(99L);
        verify(ordenRepository, never()).deleteById(99L);
    }

    @Test
    void buscarPorUsuarioId_deberiaRetornarOrdenesDelUsuario() {
        Orden orden = new Orden(1L, 10L, 20L, 399990.0, "CREADA");

        when(ordenRepository.findByUsuarioId(10L)).thenReturn(List.of(orden));

        List<Orden> resultado = ordenService.buscarPorUsuarioId(10L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getUsuarioId());

        verify(ordenRepository, times(1)).findByUsuarioId(10L);
    }

    @Test
    void totalVentas_cuandoHayVentas_deberiaRetornarTotal() {
        when(ordenRepository.totalVentas()).thenReturn(1299990.0);

        Double resultado = ordenService.totalVentas();

        assertEquals(1299990.0, resultado);

        verify(ordenRepository, times(1)).totalVentas();
    }

    @Test
    void totalVentas_cuandoNoHayVentas_deberiaRetornarCero() {
        when(ordenRepository.totalVentas()).thenReturn(null);

        Double resultado = ordenService.totalVentas();

        assertEquals(0.0, resultado);

        verify(ordenRepository, times(1)).totalVentas();
    }

    @Test
    void validarTotalOrden_conTotalValido_noDebeLanzarExcepcion() {
        assertDoesNotThrow(() -> ordenService.validarTotalOrden(100000.0));
    }

    @Test
    void validarTotalOrden_conTotalCero_deberiaLanzarBadRequestException() {
        assertThrows(BadRequestException.class, () -> ordenService.validarTotalOrden(0.0));
    }

    @Test
    void validarTotalOrden_conTotalNegativo_deberiaLanzarBadRequestException() {
        assertThrows(BadRequestException.class, () -> ordenService.validarTotalOrden(-1000.0));
    }

    @Test
    void validarTotalOrden_conTotalNulo_deberiaLanzarBadRequestException() {
        assertThrows(BadRequestException.class, () -> ordenService.validarTotalOrden(null));
    }

    private void configurarUrlServicios() {
        ReflectionTestUtils.setField(ordenService, "usuarioServiceUrl", "http://localhost:9091");
        ReflectionTestUtils.setField(ordenService, "carritoServiceUrl", "http://localhost:9092");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void configurarWebClientConRespuesta(Boolean respuesta) {
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(respuesta));
    }
}