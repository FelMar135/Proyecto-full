package com.example.producto_service.controller;

import com.example.producto_service.assembler.GpuModelAssembler;
import com.example.producto_service.dto.GpuDTO;
import com.example.producto_service.exception.GlobalExceptionHandler;
import com.example.producto_service.exception.ResourceNotFoundException;
import com.example.producto_service.model.Categoria;
import com.example.producto_service.model.Gpu;
import com.example.producto_service.service.GpuService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
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
class GpuControllerTest {

    @Mock
    private GpuService gpuService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        GpuModelAssembler assembler = new GpuModelAssembler();
        GpuController gpuController = new GpuController(gpuService, assembler);

        mockMvc = MockMvcBuilders
                .standaloneSetup(gpuController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void listarGpus_deberiaRetornarOk() throws Exception {
        when(gpuService.listar()).thenReturn(List.of(crearGpu()));

        mockMvc.perform(get("/gpus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("RTX 4060"))
                .andExpect(jsonPath("$.content[0].marca").value("NVIDIA"))
                .andExpect(jsonPath("$.content[0].categoriaId").value(1));

        verify(gpuService, times(1)).listar();
    }

    @Test
    void buscarGpuPorId_cuandoExiste_deberiaRetornarOk() throws Exception {
        when(gpuService.buscarPorId(1L)).thenReturn(crearGpu());

        mockMvc.perform(get("/gpus/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("RTX 4060"))
                .andExpect(jsonPath("$.marca").value("NVIDIA"))
                .andExpect(jsonPath("$.categoriaId").value(1));

        verify(gpuService, times(1)).buscarPorId(1L);
    }

    @Test
    void buscarGpuPorId_cuandoNoExiste_deberiaRetornarNotFound() throws Exception {
        when(gpuService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("No existe una GPU con ID: 99"));

        mockMvc.perform(get("/gpus/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));

        verify(gpuService, times(1)).buscarPorId(99L);
    }

    @Test
    void existeGpu_deberiaRetornarTrue() throws Exception {
        when(gpuService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/gpus/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(gpuService, times(1)).existePorId(1L);
    }

    @Test
    void crearGpu_deberiaRetornarOk() throws Exception {
        GpuDTO dto = crearGpuDTO();

        when(gpuService.guardar(any(Gpu.class))).thenReturn(crearGpu());

        mockMvc.perform(post("/gpus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("RTX 4060"))
                .andExpect(jsonPath("$.categoriaId").value(1));

        verify(gpuService, times(1)).guardar(any(Gpu.class));
    }

    @Test
    void actualizarGpu_deberiaRetornarOk() throws Exception {
        GpuDTO dto = crearGpuDTO();
        dto.setNombre("RTX 4070");

        Gpu actualizada = crearGpu();
        actualizada.setNombre("RTX 4070");

        when(gpuService.actualizar(eq(1L), any(Gpu.class))).thenReturn(actualizada);

        mockMvc.perform(put("/gpus/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("RTX 4070"));

        verify(gpuService, times(1)).actualizar(eq(1L), any(Gpu.class));
    }

    @Test
    void eliminarGpu_deberiaRetornarNoContent() throws Exception {
        doNothing().when(gpuService).eliminar(1L);

        mockMvc.perform(delete("/gpus/1"))
                .andExpect(status().isNoContent());

        verify(gpuService, times(1)).eliminar(1L);
    }

    private Gpu crearGpu() {
        return new Gpu(
                1L,
                "RTX 4060",
                "NVIDIA",
                "4060",
                8,
                399990L,
                "DISPONIBLE",
                10L,
                crearCategoria()
        );
    }

    private Categoria crearCategoria() {
        return new Categoria(
                1L,
                "Tarjetas gráficas",
                "GPUs para computadores gamer",
                null
        );
    }

    private GpuDTO crearGpuDTO() {
        return GpuDTO.builder()
                .id(1L)
                .nombre("RTX 4060")
                .marca("NVIDIA")
                .modelo("4060")
                .vram(8)
                .precio(399990L)
                .estado("DISPONIBLE")
                .categoriaId(1L)
                .stock(10L)
                .build();
    }
}