package com.example.carrito_service.controller;

import com.example.carrito_service.model.Carrito;
import com.example.carrito_service.service.CarritoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarritoService carritoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Faker faker;
    private Carrito carrito;

    @BeforeEach
    void setUp() {

        faker = new Faker(new Locale("es"));

        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuarioId(faker.number().numberBetween(1L, 100L));
        carrito.setGpuId(faker.number().numberBetween(100L, 500L));
        carrito.setCantidad(faker.number().numberBetween(1, 10));
    }

    @Test
    void debeObtenerCarritoPorIdYRetornarHateoas() throws Exception {

        when(carritoService.buscarPorId(1L)).thenReturn(carrito);

        mockMvc.perform(get("/carritos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carrito.getId()))
                .andExpect(jsonPath("$.usuarioId").value(carrito.getUsuarioId()))
                .andExpect(jsonPath("$.gpuId").value(carrito.getGpuId()))
                .andExpect(jsonPath("$.cantidad").value(carrito.getCantidad()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.carritos.href").exists());
    }

    @Test
    void debeObtenerTodosLosCarritos() throws Exception {

        when(carritoService.listar()).thenReturn(Arrays.asList(carrito));

        mockMvc.perform(get("/carritos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void debeCrearCarritoYRetornar201() throws Exception {

        when(carritoService.guardar(any(Carrito.class)))
                .thenReturn(carrito);

        mockMvc.perform(post("/carritos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrito)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(carrito.getId()))
                .andExpect(jsonPath("$.usuarioId").value(carrito.getUsuarioId()))
                .andExpect(jsonPath("$.gpuId").value(carrito.getGpuId()))
                .andExpect(jsonPath("$.cantidad").value(carrito.getCantidad()));
    }

    @Test
    void debeActualizarCarritoYRetornar200() throws Exception {

        when(carritoService.actualizar(eq(1L), any(Carrito.class)))
                .thenReturn(carrito);

        mockMvc.perform(put("/carritos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrito)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carrito.getId()))
                .andExpect(jsonPath("$.cantidad").value(carrito.getCantidad()));
    }

    @Test
    void debeEliminarCarritoYRetornar204() throws Exception {

        doNothing().when(carritoService).eliminar(1L);

        mockMvc.perform(delete("/carritos/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void debeVerificarExistenciaPorId() throws Exception {

        when(carritoService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/carritos/{id}/exists", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void debeObtenerCarritosPorUsuario() throws Exception {

        when(carritoService.buscarPorUsuarioId(carrito.getUsuarioId()))
                .thenReturn(Arrays.asList(carrito));

        mockMvc.perform(get("/carritos/usuario/{usuarioId}",
                        carrito.getUsuarioId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void debeObtenerTotalProductosPorUsuario() throws Exception {

        when(carritoService.totalProductosPorUsuario(
                carrito.getUsuarioId()))
                .thenReturn(5);

        mockMvc.perform(get("/carritos/usuario/{usuarioId}/total",
                        carrito.getUsuarioId()))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}

