package com.example.boleta_service.controller;

import com.example.boleta_service.assembler.BoletaModelAssembler;
import com.example.boleta_service.dto.BoletaDTO;
import com.example.boleta_service.exception.GlobalExceptionHandler;
import com.example.boleta_service.exception.ResourceNotFoundException;
import com.example.boleta_service.model.Boleta;
import com.example.boleta_service.service.BoletaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
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
class BoletaControllerTest {

    @Mock
    private BoletaService boletaService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        BoletaModelAssembler assembler = new BoletaModelAssembler();
        BoletaController controller = new BoletaController(boletaService, assembler);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void crearBoleta_deberiaRetornarOk() throws Exception {
        BoletaDTO dto = crearBoletaDTO();

        when(boletaService.guardar(any(Boleta.class))).thenReturn(crearBoleta());

        mockMvc.perform(post("/boletas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ordenId").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.total").value(119000.0));

        verify(boletaService, times(1)).guardar(any(Boleta.class));
    }

    @Test
    void listarBoletas_deberiaRetornarOk() throws Exception {
        when(boletaService.listar()).thenReturn(List.of(crearBoleta()));

        mockMvc.perform(get("/boletas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].ordenId").value(1))
                .andExpect(jsonPath("$.content[0].usuarioId").value(1))
                .andExpect(jsonPath("$.content[0].total").value(119000.0));

        verify(boletaService, times(1)).listar();
    }

    @Test
    void buscarBoletaPorId_cuandoExiste_deberiaRetornarOk() throws Exception {
        when(boletaService.buscarPorId(1L)).thenReturn(crearBoleta());

        mockMvc.perform(get("/boletas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroBoleta").value("BOL-1001"));

        verify(boletaService, times(1)).buscarPorId(1L);
    }

    @Test
    void buscarBoletaPorId_cuandoNoExiste_deberiaRetornarNotFound() throws Exception {
        when(boletaService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("No se encontró la boleta con ID: 99"));

        mockMvc.perform(get("/boletas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(boletaService, times(1)).buscarPorId(99L);
    }

    @Test
    void existeBoleta_deberiaRetornarTrue() throws Exception {
        when(boletaService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/boletas/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(boletaService, times(1)).existePorId(1L);
    }

    @Test
    void actualizarBoleta_deberiaRetornarOk() throws Exception {
        BoletaDTO dto = crearBoletaDTO();

        Boleta actualizada = crearBoleta();
        actualizada.setSubtotal(200000.0);
        actualizada.setIva(38000.0);
        actualizada.setTotal(238000.0);

        when(boletaService.actualizar(eq(1L), any(Boleta.class))).thenReturn(actualizada);

        mockMvc.perform(put("/boletas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(238000.0));

        verify(boletaService, times(1)).actualizar(eq(1L), any(Boleta.class));
    }

    @Test
    void eliminarBoleta_deberiaRetornarNoContent() throws Exception {
        doNothing().when(boletaService).eliminar(1L);

        mockMvc.perform(delete("/boletas/1"))
                .andExpect(status().isNoContent());

        verify(boletaService, times(1)).eliminar(1L);
    }

    @Test
    void buscarPorUsuario_deberiaRetornarOk() throws Exception {
        when(boletaService.buscarPorUsuarioId(1L)).thenReturn(List.of(crearBoleta()));

        mockMvc.perform(get("/boletas/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].usuarioId").value(1));

        verify(boletaService, times(1)).buscarPorUsuarioId(1L);
    }

    @Test
    void buscarPorOrden_deberiaRetornarOk() throws Exception {
        when(boletaService.buscarPorOrdenId(1L)).thenReturn(List.of(crearBoleta()));

        mockMvc.perform(get("/boletas/orden/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ordenId").value(1));

        verify(boletaService, times(1)).buscarPorOrdenId(1L);
    }

    @Test
    void totalComprado_deberiaRetornarTotal() throws Exception {
        when(boletaService.totalCompradoPorUsuario(1L)).thenReturn(357000.0);

        mockMvc.perform(get("/boletas/usuario/1/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("357000.0"));

        verify(boletaService, times(1)).totalCompradoPorUsuario(1L);
    }

    private Boleta crearBoleta() {
        return new Boleta(
                1L,
                1L,
                1L,
                100000.0,
                19000.0,
                119000.0,
                LocalDate.now(),
                "BOL-1001"
        );
    }

    private BoletaDTO crearBoletaDTO() {
        return BoletaDTO.builder()
                .id(1L)
                .ordenId(1L)
                .usuarioId(1L)
                .subtotal(100000.0)
                .iva(19000.0)
                .total(119000.0)
                .fechaEmision(LocalDate.now())
                .numeroBoleta("BOL-1001")
                .build();
    }
}