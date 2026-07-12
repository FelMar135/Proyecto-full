package com.example.carrito_service.service;

import com.example.carrito_service.exception.ResourceNotFoundException;
import com.example.carrito_service.model.Carrito;
import com.example.carrito_service.repository.CarritoRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CarritoService carritoService;

    private Faker faker;
    private Carrito carrito;

    @BeforeEach
    void setUp() {

        faker = new Faker(new Locale("es"));

        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuarioId(faker.number().numberBetween(1L, 100L));
        carrito.setGpuId(faker.number().numberBetween(1L, 100L));
        carrito.setCantidad(faker.number().numberBetween(1, 5));

        when(webClientBuilder.build()).thenReturn(webClient);

        carritoService = new CarritoService(
                carritoRepository,
                webClientBuilder
        );

        ReflectionTestUtils.setField(
                carritoService,
                "productoServiceUrl",
                "http://producto-service"
        );

        ReflectionTestUtils.setField(
                carritoService,
                "usuarioServiceUrl",
                "http://usuario-service"
        );
    }

    @SuppressWarnings("unchecked")
    private void mockValidacionesExitosas() {

        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        when(requestHeadersUriSpec.uri(anyString()))
                .thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.bodyToMono(Boolean.class))
                .thenReturn(Mono.just(true));
    }

    @Test
    void debeGuardarCarrito() {

        mockValidacionesExitosas();

        when(carritoRepository.save(any(Carrito.class)))
                .thenReturn(carrito);

        Carrito resultado = carritoService.guardar(carrito);

        assertNotNull(resultado);
        assertEquals(carrito.getId(), resultado.getId());

        verify(carritoRepository).save(carrito);
    }

    @Test
    void debeListarTodosLosCarritos() {

        when(carritoRepository.findAll())
                .thenReturn(Arrays.asList(carrito));

        List<Carrito> resultado = carritoService.listar();

        assertEquals(1, resultado.size());
        verify(carritoRepository).findAll();
    }

    @Test
    void debeBuscarCarritoPorId() {

        when(carritoRepository.findById(1L))
                .thenReturn(Optional.of(carrito));

        Carrito resultado = carritoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void debeLanzarExcepcionSiNoExisteCarrito() {

        when(carritoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> carritoService.buscarPorId(99L)
        );
    }

    @Test
    void debeVerificarExistenciaPorId() {

        when(carritoRepository.existsById(1L))
                .thenReturn(true);

        assertTrue(carritoService.existePorId(1L));
    }

    @Test
    void debeActualizarCarrito() {

        mockValidacionesExitosas();

        Carrito actualizado = new Carrito(
                1L,
                carrito.getUsuarioId() + 1,
                carrito.getGpuId() + 1,
                10
        );

        when(carritoRepository.findById(1L))
                .thenReturn(Optional.of(carrito));

        when(carritoRepository.save(any(Carrito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Carrito resultado =
                carritoService.actualizar(1L, actualizado);

        assertEquals(10, resultado.getCantidad());
        assertEquals(
                actualizado.getUsuarioId(),
                resultado.getUsuarioId()
        );

        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    void debeLanzarExcepcionSiActualizaCarritoInexistente() {

        when(carritoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> carritoService.actualizar(999L, carrito)
        );
    }

    @Test
    void debeEliminarCarrito() {

        when(carritoRepository.existsById(1L))
                .thenReturn(true);

        carritoService.eliminar(1L);

        verify(carritoRepository).deleteById(1L);
    }

    @Test
    void debeLanzarExcepcionAlEliminarInexistente() {

        when(carritoRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> carritoService.eliminar(99L)
        );

        verify(carritoRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void debeBuscarPorUsuarioId() {

        when(carritoRepository.findByUsuarioId(
                carrito.getUsuarioId()))
                .thenReturn(Arrays.asList(carrito));

        List<Carrito> resultado =
                carritoService.buscarPorUsuarioId(
                        carrito.getUsuarioId());

        assertEquals(1, resultado.size());
    }

    @Test
    void debeRetornarTotalProductosPorUsuario() {

        when(carritoRepository.totalProductosPorUsuario(
                carrito.getUsuarioId()))
                .thenReturn(8);

        Integer total =
                carritoService.totalProductosPorUsuario(
                        carrito.getUsuarioId());

        assertEquals(8, total);
    }

    @Test
    void debeRetornarCeroCuandoTotalEsNull() {

        when(carritoRepository.totalProductosPorUsuario(
                carrito.getUsuarioId()))
                .thenReturn(null);

        Integer total =
                carritoService.totalProductosPorUsuario(
                        carrito.getUsuarioId());

        assertEquals(0, total);
    }
}