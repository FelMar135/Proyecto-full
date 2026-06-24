package com.example.pago_service.controller;

import com.example.pago_service.assembler.PagoModelAssembler;
import com.example.pago_service.dto.PagoDTO;
import com.example.pago_service.service.PagoService;
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

import java.math.BigDecimal;
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
class PagoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PagoService pagoService;

    private ObjectMapper objectMapper;
    private PagoDTO pagoDTO;

    @BeforeEach
    void setUp() {
        PagoModelAssembler assembler = new PagoModelAssembler();
        PagoController controller = new PagoController(pagoService, assembler);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        pagoDTO = PagoDTO.builder()
                .id(1L)
                .ordenId(1L)
                .monto(new BigDecimal("399990"))
                .metodoPago("DEBITO")
                .estado("PAGADO")
                .fechaPago(LocalDateTime.now())
                .build();
    }

    @Test
    void testObtenerTodos() throws Exception {
        when(pagoService.obtenerTodos()).thenReturn(List.of(pagoDTO));

        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk());

        verify(pagoService, times(1)).obtenerTodos();
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(pagoService.obtenerPorId(1L)).thenReturn(pagoDTO);

        mockMvc.perform(get("/pagos/1"))
                .andExpect(status().isOk());

        verify(pagoService, times(1)).obtenerPorId(1L);
    }

    @Test
    void testExistePorId() throws Exception {
        when(pagoService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/pagos/1/exists"))
                .andExpect(status().isOk());

        verify(pagoService, times(1)).existePorId(1L);
    }

    @Test
    void testObtenerPorOrdenId() throws Exception {
        when(pagoService.obtenerPorOrdenId(1L)).thenReturn(List.of(pagoDTO));

        mockMvc.perform(get("/pagos/orden/1"))
                .andExpect(status().isOk());

        verify(pagoService, times(1)).obtenerPorOrdenId(1L);
    }

    @Test
    void testCrearPago() throws Exception {
        when(pagoService.crear(any(PagoDTO.class))).thenReturn(pagoDTO);

        mockMvc.perform(post("/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoDTO)))
                .andExpect(status().isCreated());

        verify(pagoService, times(1)).crear(any(PagoDTO.class));
    }

    @Test
    void testActualizarPago() throws Exception {
        when(pagoService.actualizar(eq(1L), any(PagoDTO.class))).thenReturn(pagoDTO);

        mockMvc.perform(put("/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoDTO)))
                .andExpect(status().isOk());

        verify(pagoService, times(1)).actualizar(eq(1L), any(PagoDTO.class));
    }

    @Test
    void testEliminarPago() throws Exception {
        doNothing().when(pagoService).eliminar(1L);

        mockMvc.perform(delete("/pagos/1"))
                .andExpect(status().isNoContent());

        verify(pagoService, times(1)).eliminar(1L);
    }
}