package com.example.orden_service.controller;

import com.example.orden_service.assembler.OrdenModelAssembler;
import com.example.orden_service.dto.OrdenDTO;
import com.example.orden_service.exception.GlobalExceptionHandler;
import com.example.orden_service.exception.ResourceNotFoundException;
import com.example.orden_service.model.Orden;
import com.example.orden_service.service.OrdenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrdenControllerTest {

    @Mock
    private OrdenService ordenService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        OrdenModelAssembler ordenModelAssembler = new OrdenModelAssembler();
        OrdenController ordenController = new OrdenController(ordenService, ordenModelAssembler);

        mockMvc = MockMvcBuilders
                .standaloneSetup(ordenController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void listarOrdenes_deberiaRetornarOk() throws Exception {
        Orden orden = new Orden(1L, 10L, 20L, 399990.0, "CREADA");

        when(ordenService.listar()).thenReturn(List.of(orden));

        mockMvc.perform(get("/ordenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].usuarioId").value(10))
                .andExpect(jsonPath("$.content[0].carritoId").value(20))
                .andExpect(jsonPath("$.content[0].estado").value("CREADA"));

        verify(ordenService, times(1)).listar();
    }

    @Test
    void buscarOrdenPorId_cuandoExiste_deberiaRetornarOk() throws Exception {
        Orden orden = new Orden(1L, 10L, 20L, 899990.0, "PAGADA");

        when(ordenService.buscarPorId(1L)).thenReturn(orden);

        mockMvc.perform(get("/ordenes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(10))
                .andExpect(jsonPath("$.carritoId").value(20))
                .andExpect(jsonPath("$.estado").value("PAGADA"));

        verify(ordenService, times(1)).buscarPorId(1L);
    }

    @Test
    void buscarOrdenPorId_cuandoNoExiste_deberiaRetornarNotFound() throws Exception {
        when(ordenService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("No existe una orden con ID: 99"));

        mockMvc.perform(get("/ordenes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));

        verify(ordenService, times(1)).buscarPorId(99L);
    }

    @Test
    void existeOrden_deberiaRetornarTrue() throws Exception {
        when(ordenService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/ordenes/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(ordenService, times(1)).existePorId(1L);
    }

    @Test
    void crearOrden_deberiaRetornarOk() throws Exception {
        OrdenDTO ordenDTO = OrdenDTO.builder()
                .usuarioId(10L)
                .carritoId(20L)
                .total(399990.0)
                .estado("CREADA")
                .build();

        Orden ordenGuardada = new Orden(1L, 10L, 20L, 399990.0, "CREADA");

        when(ordenService.guardar(any(Orden.class))).thenReturn(ordenGuardada);

        mockMvc.perform(post("/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordenDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(10))
                .andExpect(jsonPath("$.carritoId").value(20))
                .andExpect(jsonPath("$.estado").value("CREADA"));

        verify(ordenService, times(1)).guardar(any(Orden.class));
    }

    @Test
    void actualizarOrden_deberiaRetornarOk() throws Exception {
        OrdenDTO ordenDTO = OrdenDTO.builder()
                .usuarioId(10L)
                .carritoId(20L)
                .total(499990.0)
                .estado("PAGADA")
                .build();

        Orden ordenActualizada = new Orden(1L, 10L, 20L, 499990.0, "PAGADA");

        when(ordenService.actualizar(eq(1L), any(Orden.class))).thenReturn(ordenActualizada);

        mockMvc.perform(put("/ordenes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ordenDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(499990.0))
                .andExpect(jsonPath("$.estado").value("PAGADA"));

        verify(ordenService, times(1)).actualizar(eq(1L), any(Orden.class));
    }

    @Test
    void eliminarOrden_deberiaRetornarNoContent() throws Exception {
        doNothing().when(ordenService).eliminar(1L);

        mockMvc.perform(delete("/ordenes/1"))
                .andExpect(status().isNoContent());

        verify(ordenService, times(1)).eliminar(1L);
    }

    @Test
    void buscarPorUsuario_deberiaRetornarOk() throws Exception {
        Orden orden = new Orden(1L, 10L, 20L, 399990.0, "CREADA");

        when(ordenService.buscarPorUsuarioId(10L)).thenReturn(List.of(orden));

        mockMvc.perform(get("/ordenes/usuario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].usuarioId").value(10))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].carritoId").value(20))
                .andExpect(jsonPath("$.content[0].estado").value("CREADA"));

        verify(ordenService, times(1)).buscarPorUsuarioId(10L);
    }

    @Test
    void totalVentas_deberiaRetornarOk() throws Exception {
        when(ordenService.totalVentas()).thenReturn(1299990.0);

        mockMvc.perform(get("/ordenes/total-ventas"))
                .andExpect(status().isOk())
                .andExpect(content().string("1299990.0"));

        verify(ordenService, times(1)).totalVentas();
    }
}