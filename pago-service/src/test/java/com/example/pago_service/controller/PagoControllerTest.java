package com.example.pago_service.controller;

import com.example.pago_service.assembler.PagoModelAssembler;
import com.example.pago_service.dto.PagoDTO;
import com.example.pago_service.exception.GlobalExceptionHandler;
import com.example.pago_service.exception.ResourceNotFoundException;
import com.example.pago_service.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        PagoModelAssembler assembler = new PagoModelAssembler();
        PagoController controller = new PagoController(pagoService, assembler);

        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void obtenerTodos_deberiaRetornarOk() throws Exception {
        when(pagoService.obtenerTodos()).thenReturn(List.of(crearPagoDTO()));

        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].ordenId").value(1))
                .andExpect(jsonPath("$.content[0].metodoPago").value("DEBITO"))
                .andExpect(jsonPath("$.content[0].estado").value("PAGADO"));

        verify(pagoService, times(1)).obtenerTodos();
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornarOk() throws Exception {
        when(pagoService.obtenerPorId(1L)).thenReturn(crearPagoDTO());

        mockMvc.perform(get("/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ordenId").value(1))
                .andExpect(jsonPath("$.metodoPago").value("DEBITO"))
                .andExpect(jsonPath("$.estado").value("PAGADO"));

        verify(pagoService, times(1)).obtenerPorId(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaRetornarNotFound() throws Exception {
        when(pagoService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("No existe un pago con ID: 99"));

        mockMvc.perform(get("/pagos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));

        verify(pagoService, times(1)).obtenerPorId(99L);
    }

    @Test
    void existePorId_deberiaRetornarTrue() throws Exception {
        when(pagoService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/pagos/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(pagoService, times(1)).existePorId(1L);
    }

    @Test
    void obtenerPorOrdenId_deberiaRetornarOk() throws Exception {
        when(pagoService.obtenerPorOrdenId(1L)).thenReturn(List.of(crearPagoDTO()));

        mockMvc.perform(get("/pagos/orden/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ordenId").value(1))
                .andExpect(jsonPath("$.content[0].estado").value("PAGADO"));

        verify(pagoService, times(1)).obtenerPorOrdenId(1L);
    }

    @Test
    void crear_deberiaRetornarCreatedConHateoas() throws Exception {
        PagoDTO dto = crearPagoDTO();

        when(pagoService.crear(any(PagoDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ordenId").value(1))
                .andExpect(jsonPath("$.metodoPago").value("DEBITO"))
                .andExpect(jsonPath("$.estado").value("PAGADO"));

        verify(pagoService, times(1)).crear(any(PagoDTO.class));
    }

    @Test
    void actualizar_deberiaRetornarOkConHateoas() throws Exception {
        PagoDTO dto = crearPagoDTO();
        dto.setEstado("PENDIENTE");

        when(pagoService.actualizar(eq(1L), any(PagoDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(pagoService, times(1)).actualizar(eq(1L), any(PagoDTO.class));
    }

    @Test
    void eliminar_deberiaRetornarNoContent() throws Exception {
        doNothing().when(pagoService).eliminar(1L);

        mockMvc.perform(delete("/pagos/1"))
                .andExpect(status().isNoContent());

        verify(pagoService, times(1)).eliminar(1L);
    }

    private PagoDTO crearPagoDTO() {
        return PagoDTO.builder()
                .id(1L)
                .ordenId(1L)
                .monto(new BigDecimal("399990"))
                .metodoPago("DEBITO")
                .estado("PAGADO")
                .fechaPago(LocalDateTime.now())
                .build();
    }
}