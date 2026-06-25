package com.example.resena_service.controller;

import com.example.resena_service.assembler.ResenaModelAssembler;
import com.example.resena_service.dto.ResenaDTO;
import com.example.resena_service.service.ResenaService;
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

// 1. Cargamos SOLO el controlador de reseñas (hace que el test sea muy rápido)
@WebMvcTest(ResenaController.class)
// 2. Importamos el Assembler REAL para probar que los links HATEOAS se generan bien
@Import(ResenaModelAssembler.class)
public class ResenaControllerTest {

    @Autowired
    private MockMvc mockMvc; // Nuestro "Postman" interno

    @MockBean
    private ResenaService resenaService; // Clon falso del servicio

    @Autowired
    private ObjectMapper objectMapper; // Convierte objetos Java a JSON y viceversa

    // Agregamos Faker
    private Faker faker;
    private ResenaDTO resenaDTO;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("es"));

        resenaDTO = new ResenaDTO();
        resenaDTO.setId(1L); // Dejamos el ID fijo en 1 para las rutas simuladas
        resenaDTO.setUsuarioId(faker.number().numberBetween(1L, 100L));
        resenaDTO.setGpuId(faker.number().numberBetween(100L, 500L));
        resenaDTO.setComentario(faker.lorem().sentence(8)); // Comentario dinámico
        resenaDTO.setCalificacion(faker.number().numberBetween(1, 6)); // Calificación entre 1 y 5
        resenaDTO.setFecha(LocalDate.now());
    }

    @Test
    void debeObtenerResenaPorIdYRetornarHateoas() throws Exception {
        // GIVEN: Si el controlador pide el ID 1, el servicio falso devuelve nuestra reseña de Faker
        when(resenaService.findById(1L)).thenReturn(resenaDTO);

        // WHEN & THEN: Simulamos un GET a /resenas/1
        mockMvc.perform(get("/resenas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Verificamos el código HTTP 200 OK
                .andExpect(status().isOk())
                // Verificamos los datos del JSON dinámicamente usando el objeto resenaDTO
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.calificacion").value(resenaDTO.getCalificacion()))
                .andExpect(jsonPath("$.comentario").value(resenaDTO.getComentario()))
                // Verificamos que el Assembler pegó los enlaces HATEOAS correctamente
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todas-las-resenas.href").exists());
    }

    @Test
    void debeObtenerTodasLasResenas() throws Exception {
        when(resenaService.findAll()).thenReturn(Arrays.asList(resenaDTO));

        mockMvc.perform(get("/resenas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // ._embedded.resenaDTOList es la estructura estándar de HATEOAS para listas
                .andExpect(jsonPath("$._embedded.resenaDTOList[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.resenaDTOList[0].comentario").value(resenaDTO.getComentario()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void debeCrearResenaExitosaYRetornar201() throws Exception {
        // GIVEN: Cuando se guarde cualquier reseña, devuelve la reseña creada por Faker
        when(resenaService.save(any(ResenaDTO.class))).thenReturn(resenaDTO);

        // WHEN & THEN: Simulamos un POST enviando el JSON
        mockMvc.perform(post("/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resenaDTO))) // Convertimos el DTO a JSON String
                // Verificamos el código HTTP 201 Created
                .andExpect(status().isCreated())
                // Verificamos que exista la cabecera Location (buena práctica REST)
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.comentario").value(resenaDTO.getComentario()));
    }

    @Test
    void debeEliminarResenaYRetornar204() throws Exception {
        // GIVEN: Al eliminar, no hace nada (void)
        doNothing().when(resenaService).deleteById(1L);

        // WHEN & THEN: Simulamos el DELETE
        mockMvc.perform(delete("/resenas/{id}", 1L))
                // Verificamos el código HTTP 204 No Content
                .andExpect(status().isNoContent());
    }

    @Test
    void debeRetornar404CuandoResenaNoExiste() throws Exception {
        // GIVEN: Forzamos que el servicio tire la excepción que creaste
        when(resenaService.findById(99L))
            .thenThrow(new com.example.resena_service.exception.ResourceNotFoundException("Reseña no encontrada"));

        // WHEN & THEN: Hacemos la petición y esperamos un código 404 y tu JSON de error
        mockMvc.perform(get("/resenas/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));
    }

    @Test
    void debeActualizarResenaYRetornar200() throws Exception {
        // Simulamos que el servicio actualiza la reseña y nos devuelve la modificada
        when(resenaService.update(eq(1L), any(ResenaDTO.class))).thenReturn(resenaDTO);

        mockMvc.perform(put("/resenas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resenaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comentario").value(resenaDTO.getComentario()));
    }

    @Test
    void debeObtenerResenasPorUsuario() throws Exception {
        when(resenaService.findByUsuarioId(resenaDTO.getUsuarioId())).thenReturn(Arrays.asList(resenaDTO));

        mockMvc.perform(get("/resenas/usuario/{usuarioId}", resenaDTO.getUsuarioId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.resenaDTOList[0].usuarioId").value(resenaDTO.getUsuarioId()));
    }

    @Test
    void debeObtenerResenasPorGpu() throws Exception {
        when(resenaService.findByGpuId(resenaDTO.getGpuId())).thenReturn(Arrays.asList(resenaDTO));

        mockMvc.perform(get("/resenas/gpu/{gpuId}", resenaDTO.getGpuId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.resenaDTOList[0].gpuId").value(resenaDTO.getGpuId()));
    }
}