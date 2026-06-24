package com.example.envio_service.controller;

import com.example.envio_service.assembler.EnvioModelAssembler;
import com.example.envio_service.dto.EnvioDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EnvioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnvioService envioService;

    private ObjectMapper objectMapper;
    private EnvioDTO envioDTO;

    @BeforeEach
    void setUp() {
        EnvioModelAssembler assembler = new EnvioModelAssembler();
        EnvioController controller = new EnvioController(envioService, assembler);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        envioDTO = EnvioDTO.builder()
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

    @Test
    void testObtenerTodos() throws Exception {
        when(envioService.obtenerTodos()).thenReturn(List.of(envioDTO));

        mockMvc.perform(get("/envios"))
                .andExpect(status().isOk());

        verify(envioService, times(1)).obtenerTodos();
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(envioService.obtenerPorId(1L)).thenReturn(envioDTO);

        mockMvc.perform(get("/envios/1"))
                .andExpect(status().isOk());

        verify(envioService, times(1)).obtenerPorId(1L);
    }

    @Test
    void testExistePorId() throws Exception {
        when(envioService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/envios/1/exists"))
                .andExpect(status().isOk());

        verify(envioService, times(1)).existePorId(1L);
    }

    @Test
    void testCrearEnvio() throws Exception {
        when(envioService.crear(any(EnvioDTO.class))).thenReturn(envioDTO);

        mockMvc.perform(post("/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(envioDTO)))
                .andExpect(status().isCreated());

        verify(envioService, times(1)).crear(any(EnvioDTO.class));
    }

    @Test
    void testActualizarEnvio() throws Exception {
        when(envioService.actualizar(eq(1L), any(EnvioDTO.class))).thenReturn(envioDTO);

        mockMvc.perform(put("/envios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(envioDTO)))
                .andExpect(status().isOk());

        verify(envioService, times(1)).actualizar(eq(1L), any(EnvioDTO.class));
    }

    @Test
    void testEliminarEnvio() throws Exception {
        doNothing().when(envioService).eliminar(1L);

        mockMvc.perform(delete("/envios/1"))
                .andExpect(status().isNoContent());

        verify(envioService, times(1)).eliminar(1L);
    }
}