package com.example.soporte_service.controller;

import com.example.soporte_service.assembler.SoporteModelAssembler;
import com.example.soporte_service.dto.SoporteDTO;
import com.example.soporte_service.service.SoporteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SoporteController.class)
@Import(SoporteModelAssembler.class)
public class SoporteControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private SoporteService soporteService;
    @Autowired private ObjectMapper objectMapper;

    private Faker faker;
    private SoporteDTO soporteDTO;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("es"));
        soporteDTO = new SoporteDTO();
        soporteDTO.setId(1L);
        soporteDTO.setUsuarioId(faker.number().numberBetween(1L, 100L));
        soporteDTO.setOrdenId(faker.number().numberBetween(200L, 500L));
        soporteDTO.setAsunto(faker.lorem().sentence(4));
        soporteDTO.setDescripcion(faker.lorem().paragraph());
        soporteDTO.setEstado("ABIERTO");
        soporteDTO.setFechaCreacion(LocalDate.now());
    }

    @Test
    void debeObtenerTicketPorIdYRetornarHateoas() throws Exception {
        when(soporteService.findById(1L)).thenReturn(soporteDTO);
        mockMvc.perform(get("/soporte/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void debeObtenerTodosLosTickets() throws Exception {
        when(soporteService.findAll()).thenReturn(Arrays.asList(soporteDTO));
        mockMvc.perform(get("/soporte").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.soporteDTOList[0].id").value(1L));
    }

    @Test
    void debeCrearTicketExitosoYRetornar201() throws Exception {
        when(soporteService.save(any(SoporteDTO.class))).thenReturn(soporteDTO);
        mockMvc.perform(post("/soporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(soporteDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void debeActualizarTicketYRetornar200() throws Exception {
        when(soporteService.update(eq(1L), any(SoporteDTO.class))).thenReturn(soporteDTO);
        mockMvc.perform(put("/soporte/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(soporteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asunto").value(soporteDTO.getAsunto()));
    }

    @Test
    void debeEliminarTicketYRetornar204() throws Exception {
        doNothing().when(soporteService).deleteById(1L);
        mockMvc.perform(delete("/soporte/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void debeObtenerTicketsPorUsuario() throws Exception {
        when(soporteService.findByUsuarioId(soporteDTO.getUsuarioId())).thenReturn(Arrays.asList(soporteDTO));
        mockMvc.perform(get("/soporte/usuario/{usuarioId}", soporteDTO.getUsuarioId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void debeObtenerTicketsPorOrden() throws Exception {
        when(soporteService.findByOrdenId(soporteDTO.getOrdenId())).thenReturn(Arrays.asList(soporteDTO));
        mockMvc.perform(get("/soporte/orden/{ordenId}", soporteDTO.getOrdenId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void debeRetornar404CuandoSoporteNoExiste() throws Exception {
        // Simulamos que al buscar, el servicio lanza la excepción de No Encontrado
        when(soporteService.findById(99L)).thenThrow(new com.example.soporte_service.exception.ResourceNotFoundException("No encontrado"));

        mockMvc.perform(get("/soporte/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                // CORREGIDO: AHORA ESPERA "Not Found"
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void debeRetornar400CuandoHayBadRequest() throws Exception {
        // Simulamos que al guardar, falla la validación del usuario o la orden
        when(soporteService.save(any(SoporteDTO.class))).thenThrow(new com.example.soporte_service.exception.BadRequestException("Mal request"));

        mockMvc.perform(post("/soporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(soporteDTO)))
                .andExpect(status().isBadRequest())
                // CORREGIDO: AHORA ESPERA "Bad Request"
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void debeRetornar500CuandoHayErrorInterno() throws Exception {
        // Simulamos un error fatal del servidor (ej. se cayó la base de datos)
        when(soporteService.findAll()).thenThrow(new RuntimeException("Error fatal"));

        mockMvc.perform(get("/soporte")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                // CORREGIDO: AHORA ESPERA "Internal Server Error"
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }
}