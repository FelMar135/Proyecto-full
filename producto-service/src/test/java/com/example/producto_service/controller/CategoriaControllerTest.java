package com.example.producto_service.controller;

import com.example.producto_service.assembler.CategoriaModelAssembler;
import com.example.producto_service.dto.CategoriaDTO;
import com.example.producto_service.exception.GlobalExceptionHandler;
import com.example.producto_service.exception.ResourceNotFoundException;
import com.example.producto_service.model.Categoria;
import com.example.producto_service.service.CategoriaService;
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
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        CategoriaModelAssembler assembler = new CategoriaModelAssembler();
        CategoriaController categoriaController = new CategoriaController(categoriaService, assembler);

        mockMvc = MockMvcBuilders
                .standaloneSetup(categoriaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void listarCategorias_deberiaRetornarOk() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of(crearCategoria()));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("Tarjetas gráficas"))
                .andExpect(jsonPath("$.content[0].descripcion").value("GPUs para computadores gamer"));

        verify(categoriaService, times(1)).listar();
    }

    @Test
    void buscarCategoriaPorId_cuandoExiste_deberiaRetornarOk() throws Exception {
        when(categoriaService.buscarPorId(1L)).thenReturn(crearCategoria());

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Tarjetas gráficas"))
                .andExpect(jsonPath("$.descripcion").value("GPUs para computadores gamer"));

        verify(categoriaService, times(1)).buscarPorId(1L);
    }

    @Test
    void buscarCategoriaPorId_cuandoNoExiste_deberiaRetornarNotFound() throws Exception {
        when(categoriaService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("No existe una categoría con ID: 99"));

        mockMvc.perform(get("/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));

        verify(categoriaService, times(1)).buscarPorId(99L);
    }

    @Test
    void existeCategoria_deberiaRetornarTrue() throws Exception {
        when(categoriaService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/categorias/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(categoriaService, times(1)).existePorId(1L);
    }

    @Test
    void crearCategoria_deberiaRetornarOk() throws Exception {
        CategoriaDTO dto = crearCategoriaDTO();

        when(categoriaService.guardar(any(Categoria.class))).thenReturn(crearCategoria());

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Tarjetas gráficas"));

        verify(categoriaService, times(1)).guardar(any(Categoria.class));
    }

    @Test
    void actualizarCategoria_deberiaRetornarOk() throws Exception {
        CategoriaDTO dto = crearCategoriaDTO();
        dto.setNombre("Componentes");

        Categoria categoriaActualizada = crearCategoria();
        categoriaActualizada.setNombre("Componentes");

        when(categoriaService.actualizar(eq(1L), any(Categoria.class))).thenReturn(categoriaActualizada);

        mockMvc.perform(put("/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Componentes"));

        verify(categoriaService, times(1)).actualizar(eq(1L), any(Categoria.class));
    }

    @Test
    void eliminarCategoria_deberiaRetornarNoContent() throws Exception {
        doNothing().when(categoriaService).eliminar(1L);

        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService, times(1)).eliminar(1L);
    }

    private Categoria crearCategoria() {
        return new Categoria(
                1L,
                "Tarjetas gráficas",
                "GPUs para computadores gamer",
                null
        );
    }

    private CategoriaDTO crearCategoriaDTO() {
        return CategoriaDTO.builder()
                .id(1L)
                .nombre("Tarjetas gráficas")
                .descripcion("GPUs para computadores gamer")
                .build();
    }
}