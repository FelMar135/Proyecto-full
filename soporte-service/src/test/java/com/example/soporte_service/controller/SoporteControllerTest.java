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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SoporteController.class)
@Import(SoporteModelAssembler.class)
public class SoporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SoporteService soporteService;

    @Autowired
    private ObjectMapper objectMapper;

    // Declaramos Faker y nuestro DTO global
    private Faker faker;
    private SoporteDTO soporteDTO;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("es"));

        soporteDTO = new SoporteDTO();
        soporteDTO.setId(1L); // ID fijo para facilitar las rutas simuladas
        soporteDTO.setUsuarioId(faker.number().numberBetween(1L, 100L));
        soporteDTO.setOrdenId(faker.number().numberBetween(200L, 500L));
        soporteDTO.setAsunto(faker.lorem().sentence(4));
        soporteDTO.setDescripcion(faker.lorem().paragraph());
        soporteDTO.setEstado(faker.options().option("ABIERTO", "EN_PROCESO"));
        soporteDTO.setFechaCreacion(LocalDate.now());
    }

    @Test
    void debeObtenerTicketPorIdYRetornarHateoas() throws Exception {
        when(soporteService.findById(1L)).thenReturn(soporteDTO);

        mockMvc.perform(get("/soporte/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Verificamos que el ID y el Asunto coincidan con lo que generó Faker
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.asunto").value(soporteDTO.getAsunto()))
                .andExpect(jsonPath("$.estado").value(soporteDTO.getEstado()))
                // Verificamos los enlaces principales de tu SoporteModelAssembler
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-los-tickets.href").exists())
                .andExpect(jsonPath("$._links.tickets-por-orden.href").exists());
    }

    @Test
    void debeObtenerTodosLosTickets() throws Exception {
        when(soporteService.findAll()).thenReturn(Arrays.asList(soporteDTO));

        mockMvc.perform(get("/soporte")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Verificamos el contenido de la lista HATEOAS
                .andExpect(jsonPath("$._embedded.soporteDTOList[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.soporteDTOList[0].asunto").value(soporteDTO.getAsunto()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void debeCrearTicketExitosoYRetornar201() throws Exception {
        when(soporteService.save(any(SoporteDTO.class))).thenReturn(soporteDTO);

        mockMvc.perform(post("/soporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertimos el DTO con datos de Faker a un String JSON
                        .content(objectMapper.writeValueAsString(soporteDTO)))
                .andExpect(status().isCreated())
                // Verificamos la cabecera Location que agregaste en tu controlador
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.asunto").value(soporteDTO.getAsunto()));
    }

    @Test
    void debeEliminarTicketYRetornar204() throws Exception {
        doNothing().when(soporteService).deleteById(1L);

        mockMvc.perform(delete("/soporte/{id}", 1L))
                .andExpect(status().isNoContent()); // 204 No Content
    }
}