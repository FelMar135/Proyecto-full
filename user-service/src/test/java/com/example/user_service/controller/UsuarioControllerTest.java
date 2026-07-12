package com.example.user_service.controller;

import com.example.user_service.assembler.UsuarioModelAssembler;
import com.example.user_service.dto.UsuarioDTO;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.model.Usuario;
import com.example.user_service.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import(UsuarioModelAssembler.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Faker faker;
    private Usuario usuario;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {

        faker = new Faker(new Locale("es"));

        usuario = new Usuario(
                1L,
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                faker.phoneNumber().cellPhone(),
                faker.address().fullAddress()
        );

        usuarioDTO = UsuarioDTO.fromModel(usuario);
    }

    @Test
    void debeCrearUsuario() throws Exception {

        when(usuarioService.guardar(any(Usuario.class)))
                .thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nombre").value(usuarioDTO.getNombre()))
                .andExpect(jsonPath("$.email").value(usuarioDTO.getEmail()));
    }

    @Test
    void debeListarUsuarios() throws Exception {

        when(usuarioService.listar())
                .thenReturn(Arrays.asList(usuario));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usuarioDTOList[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.usuarioDTOList[0].nombre")
                        .value(usuarioDTO.getNombre()));
    }

    @Test
    void debeBuscarUsuarioPorId() throws Exception {

        when(usuarioService.buscarPorId(1L))
                .thenReturn(usuario);

        mockMvc.perform(get("/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre")
                        .value(usuarioDTO.getNombre()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void debeRetornar404CuandoUsuarioNoExiste() throws Exception {

        when(usuarioService.buscarPorId(99L))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Usuario no encontrado con ID: 99"
                        )
                );

        mockMvc.perform(get("/usuarios/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void debeActualizarUsuario() throws Exception {

        when(usuarioService.actualizar(
                eq(1L),
                any(Usuario.class)))
                .thenReturn(usuario);

        mockMvc.perform(put("/usuarios/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value(usuarioDTO.getNombre()))
                .andExpect(jsonPath("$.email")
                        .value(usuarioDTO.getEmail()));
    }

    @Test
    void debeEliminarUsuario() throws Exception {

        doNothing().when(usuarioService)
                .eliminar(1L);

        mockMvc.perform(delete("/usuarios/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void debeVerificarExistenciaUsuario() throws Exception {

        when(usuarioService.existePorId(1L))
                .thenReturn(true);

        mockMvc.perform(get("/usuarios/{id}/exists", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}