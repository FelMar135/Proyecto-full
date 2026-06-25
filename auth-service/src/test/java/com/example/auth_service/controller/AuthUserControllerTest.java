package com.example.auth_service.controller;

import com.example.auth_service.assembler.AuthUserModelAssembler;
import com.example.auth_service.exception.GlobalExceptionHandler;
import com.example.auth_service.exception.ResourceNotFoundException;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.service.AuthUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class AuthUserControllerTest {

    @Mock
    private AuthUserService authUserService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AuthUserModelAssembler assembler = new AuthUserModelAssembler();
        AuthUserController controller = new AuthUserController(authUserService, assembler);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void login_deberiaRetornarToken() throws Exception {
        AuthUser request = new AuthUser(null, null, "admin@gpustore.cl", "123456", null);

        when(authUserService.login("admin@gpustore.cl", "123456")).thenReturn("token.jwt");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("token.jwt"));

        verify(authUserService, times(1)).login("admin@gpustore.cl", "123456");
    }

    @Test
    void register_deberiaRetornarMensaje() throws Exception {
        AuthUser request = crearUsuario();

        when(authUserService.register(any(AuthUser.class))).thenReturn("Usuario creado exitosamente");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario creado exitosamente"));

        verify(authUserService, times(1)).register(any(AuthUser.class));
    }

    @Test
    void crearUsuario_deberiaRetornarOk() throws Exception {
        AuthUser request = crearUsuario();

        when(authUserService.guardar(any(AuthUser.class))).thenReturn(crearUsuario());

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@gpustore.cl"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));

        verify(authUserService, times(1)).guardar(any(AuthUser.class));
    }

    @Test
    void listarUsuarios_deberiaRetornarOk() throws Exception {
        when(authUserService.listar()).thenReturn(List.of(crearUsuario()));

        mockMvc.perform(get("/auth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].username").value("admin"))
                .andExpect(jsonPath("$.content[0].email").value("admin@gpustore.cl"))
                .andExpect(jsonPath("$.content[0].rol").value("ADMIN"));

        verify(authUserService, times(1)).listar();
    }

    @Test
    void buscarUsuarioPorId_cuandoExiste_deberiaRetornarOk() throws Exception {
        when(authUserService.buscarPorId(1L)).thenReturn(crearUsuario());

        mockMvc.perform(get("/auth/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@gpustore.cl"));

        verify(authUserService, times(1)).buscarPorId(1L);
    }

    @Test
    void buscarUsuarioPorId_cuandoNoExiste_deberiaRetornarNotFound() throws Exception {
        when(authUserService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("No existe un usuario auth con ID: 99"));

        mockMvc.perform(get("/auth/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));

        verify(authUserService, times(1)).buscarPorId(99L);
    }

    @Test
    void existeUsuario_deberiaRetornarTrue() throws Exception {
        when(authUserService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/auth/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(authUserService, times(1)).existePorId(1L);
    }

    @Test
    void buscarUsuarioPorEmail_deberiaRetornarOk() throws Exception {
        when(authUserService.buscarPorEmail("admin@gpustore.cl")).thenReturn(crearUsuario());

        mockMvc.perform(get("/auth/email/admin@gpustore.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("admin@gpustore.cl"));

        verify(authUserService, times(1)).buscarPorEmail("admin@gpustore.cl");
    }

    @Test
    void obtenerRolPorEmail_deberiaRetornarRol() throws Exception {
        when(authUserService.getRole("admin@gpustore.cl")).thenReturn("ADMIN");

        mockMvc.perform(get("/auth/role/admin@gpustore.cl"))
                .andExpect(status().isOk())
                .andExpect(content().string("ADMIN"));

        verify(authUserService, times(1)).getRole("admin@gpustore.cl");
    }

    @Test
    void actualizarUsuario_deberiaRetornarOk() throws Exception {
        AuthUser actualizado = new AuthUser(
                1L,
                "matias",
                "matias@gpustore.cl",
                "hashNuevo",
                "USER"
        );

        when(authUserService.actualizar(eq(1L), any(AuthUser.class))).thenReturn(actualizado);

        mockMvc.perform(put("/auth/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("matias"))
                .andExpect(jsonPath("$.email").value("matias@gpustore.cl"));

        verify(authUserService, times(1)).actualizar(eq(1L), any(AuthUser.class));
    }

    @Test
    void eliminarUsuario_deberiaRetornarNoContent() throws Exception {
        doNothing().when(authUserService).eliminar(1L);

        mockMvc.perform(delete("/auth/1"))
                .andExpect(status().isNoContent());

        verify(authUserService, times(1)).eliminar(1L);
    }

    private AuthUser crearUsuario() {
        return new AuthUser(
                1L,
                "admin",
                "admin@gpustore.cl",
                "hash123",
                "ADMIN"
        );
    }
}