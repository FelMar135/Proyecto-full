package com.example.envio_service.controller;

import com.example.envio_service.assembler.EnvioModelAssembler;
import com.example.envio_service.dto.EnvioDTO;
import com.example.envio_service.exception.GlobalExceptionHandler;
import com.example.envio_service.exception.ResourceNotFoundException;
import com.example.envio_service.service.EnvioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
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
class EnvioControllerTest {

    @Mock
    private EnvioService envioService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        EnvioModelAssembler assembler = new EnvioModelAssembler();
        EnvioController controller = new EnvioController(envioService, assembler);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void obtenerTodos_deberiaRetornarOk() throws Exception {
        when(envioService.obtenerTodos()).thenReturn(List.of(crearEnvioDTO()));

        mockMvc.perform(get("/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].ordenId").value(1))
                .andExpect(jsonPath("$.content[0].ciudad").value("Santiago"));

        verify(envioService, times(1)).obtenerTodos();
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornarOk() throws Exception {
        when(envioService.obtenerPorId(1L)).thenReturn(crearEnvioDTO());

        mockMvc.perform(get("/envios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ordenId").value(1))
                .andExpect(jsonPath("$.estado").value("EN_PREPARACION"));

        verify(envioService, times(1)).obtenerPorId(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaRetornarNotFound() throws Exception {
        when(envioService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("No existe un envío con ID: 99"));

        mockMvc.perform(get("/envios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));

        verify(envioService, times(1)).obtenerPorId(99L);
    }

    @Test
    void existePorId_deberiaRetornarTrue() throws Exception {
        when(envioService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/envios/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(envioService, times(1)).existePorId(1L);
    }

    @Test
    void obtenerPorOrdenId_deberiaRetornarOk() throws Exception {
        when(envioService.obtenerPorOrdenId(1L)).thenReturn(List.of(crearEnvioDTO()));

        mockMvc.perform(get("/envios/orden/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ordenId").value(1));

        verify(envioService, times(1)).obtenerPorOrdenId(1L);
    }

    @Test
    void obtenerPorEstado_deberiaRetornarOk() throws Exception {
        when(envioService.obtenerPorEstado("EN_PREPARACION")).thenReturn(List.of(crearEnvioDTO()));

        mockMvc.perform(get("/envios/estado/EN_PREPARACION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].estado").value("EN_PREPARACION"));

        verify(envioService, times(1)).obtenerPorEstado("EN_PREPARACION");
    }

    @Test
    void obtenerPorCiudad_deberiaRetornarOk() throws Exception {
        when(envioService.obtenerPorCiudad("Santiago")).thenReturn(List.of(crearEnvioDTO()));

        mockMvc.perform(get("/envios/ciudad/Santiago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ciudad").value("Santiago"));

        verify(envioService, times(1)).obtenerPorCiudad("Santiago");
    }

    @Test
    void crear_deberiaRetornarCreatedConHateoas() throws Exception {
        EnvioDTO dto = crearEnvioDTO();

        when(envioService.crear(any(EnvioDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ordenId").value(1))
                .andExpect(jsonPath("$.numeroSeguimiento").value("ENV-1001"));

        verify(envioService, times(1)).crear(any(EnvioDTO.class));
    }

    @Test
    void actualizar_deberiaRetornarOkConHateoas() throws Exception {
        EnvioDTO dto = crearEnvioDTO();
        dto.setEstado("EN_TRANSITO");

        when(envioService.actualizar(eq(1L), any(EnvioDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/envios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("EN_TRANSITO"));

        verify(envioService, times(1)).actualizar(eq(1L), any(EnvioDTO.class));
    }

    @Test
    void eliminar_deberiaRetornarNoContent() throws Exception {
        doNothing().when(envioService).eliminar(1L);

        mockMvc.perform(delete("/envios/1"))
                .andExpect(status().isNoContent());

        verify(envioService, times(1)).eliminar(1L);
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