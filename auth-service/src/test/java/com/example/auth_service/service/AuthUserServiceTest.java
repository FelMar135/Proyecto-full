package com.example.auth_service.service;

import com.example.auth_service.exception.BadRequestException;
import com.example.auth_service.exception.ResourceNotFoundException;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.repository.AuthUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUserServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HashService hashService;

    @InjectMocks
    private AuthUserService authUserService;

    @Test
    void login_conCredencialesValidas_deberiaRetornarToken() {
        AuthUser user = crearUsuario();
        user.setPassword("hash123");

        when(authUserRepository.findByEmail("admin@gpustore.cl")).thenReturn(user);
        when(hashService.sha1("123456")).thenReturn("hash123");
        when(jwtService.generateToken("admin@gpustore.cl")).thenReturn("token.jwt");

        String resultado = authUserService.login("admin@gpustore.cl", "123456");

        assertEquals("token.jwt", resultado);

        verify(authUserRepository, times(1)).findByEmail("admin@gpustore.cl");
        verify(hashService, times(1)).sha1("123456");
        verify(jwtService, times(1)).generateToken("admin@gpustore.cl");
    }

    @Test
    void login_sinEmail_deberiaLanzarBadRequestException() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authUserService.login("", "123456")
        );

        assertEquals("El email es obligatorio", exception.getMessage());
    }

    @Test
    void login_sinPassword_deberiaLanzarBadRequestException() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authUserService.login("admin@gpustore.cl", "")
        );

        assertEquals("La contraseña es obligatoria", exception.getMessage());
    }

    @Test
    void login_conUsuarioInexistente_deberiaLanzarBadRequestException() {
        when(authUserRepository.findByEmail("noexiste@gpustore.cl")).thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authUserService.login("noexiste@gpustore.cl", "123456")
        );

        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    void login_conPasswordIncorrecta_deberiaLanzarBadRequestException() {
        AuthUser user = crearUsuario();
        user.setPassword("hashCorrecto");

        when(authUserRepository.findByEmail("admin@gpustore.cl")).thenReturn(user);
        when(hashService.sha1("mala")).thenReturn("hashMalo");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authUserService.login("admin@gpustore.cl", "mala")
        );

        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    void register_conDatosValidos_deberiaCrearUsuario() {
        AuthUser user = crearUsuario();
        user.setId(null);

        when(authUserRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(hashService.sha1("123456")).thenReturn("hash123");
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(user);

        String resultado = authUserService.register(user);

        assertEquals("Usuario creado exitosamente", resultado);
        assertEquals("hash123", user.getPassword());
        assertEquals("USER", user.getRol());

        verify(authUserRepository, times(1)).save(user);
    }

    @Test
    void register_conEmailExistente_deberiaLanzarBadRequestException() {
        AuthUser user = crearUsuario();

        when(authUserRepository.findByEmail(user.getEmail())).thenReturn(user);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authUserService.register(user)
        );

        assertEquals("Ya existe un usuario registrado con ese email", exception.getMessage());
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void guardar_conDatosValidos_deberiaGuardarUsuario() {
        AuthUser user = crearUsuario();

        when(authUserRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(hashService.sha1("123456")).thenReturn("hash123");
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(user);

        AuthUser resultado = authUserService.guardar(user);

        assertNotNull(resultado);
        assertEquals("hash123", resultado.getPassword());

        verify(authUserRepository, times(1)).save(user);
    }

    @Test
    void guardar_conUsuarioNulo_deberiaLanzarBadRequestException() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authUserService.guardar(null)
        );

        assertEquals("El usuario auth no puede ser nulo", exception.getMessage());
    }

    @Test
    void listar_deberiaRetornarUsuarios() {
        when(authUserRepository.findAll()).thenReturn(List.of(crearUsuario()));

        List<AuthUser> resultado = authUserService.listar();

        assertEquals(1, resultado.size());
        assertEquals("admin", resultado.get(0).getUsername());

        verify(authUserRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarUsuario() {
        when(authUserRepository.findById(1L)).thenReturn(Optional.of(crearUsuario()));

        AuthUser resultado = authUserService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        verify(authUserRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(authUserRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authUserService.buscarPorId(99L)
        );

        assertEquals("No existe un usuario auth con ID: 99", exception.getMessage());
    }

    @Test
    void existePorId_deberiaRetornarTrue() {
        when(authUserRepository.existsById(1L)).thenReturn(true);

        boolean resultado = authUserService.existePorId(1L);

        assertTrue(resultado);
        verify(authUserRepository, times(1)).existsById(1L);
    }

    @Test
    void buscarPorEmail_cuandoExiste_deberiaRetornarUsuario() {
        when(authUserRepository.findByEmail("admin@gpustore.cl")).thenReturn(crearUsuario());

        AuthUser resultado = authUserService.buscarPorEmail("admin@gpustore.cl");

        assertNotNull(resultado);
        assertEquals("admin@gpustore.cl", resultado.getEmail());
    }

    @Test
    void buscarPorEmail_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(authUserRepository.findByEmail("noexiste@gpustore.cl")).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authUserService.buscarPorEmail("noexiste@gpustore.cl")
        );

        assertEquals("No existe un usuario auth con email: noexiste@gpustore.cl", exception.getMessage());
    }

    @Test
    void actualizar_cuandoExiste_deberiaActualizarUsuario() {
        AuthUser existente = crearUsuario();

        AuthUser actualizado = new AuthUser(
                1L,
                "matias",
                "matias@gpustore.cl",
                "nueva123",
                "ADMIN"
        );

        when(authUserRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(authUserRepository.findByEmail("matias@gpustore.cl")).thenReturn(null);
        when(hashService.sha1("nueva123")).thenReturn("hashNuevo");
        when(authUserRepository.save(any(AuthUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthUser resultado = authUserService.actualizar(1L, actualizado);

        assertEquals("matias", resultado.getUsername());
        assertEquals("matias@gpustore.cl", resultado.getEmail());
        assertEquals("hashNuevo", resultado.getPassword());
        assertEquals("ADMIN", resultado.getRol());

        verify(authUserRepository, times(1)).save(existente);
    }

    @Test
    void actualizar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(authUserRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authUserService.actualizar(99L, crearUsuario())
        );

        assertEquals("No existe un usuario auth con ID: 99", exception.getMessage());
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminarUsuario() {
        when(authUserRepository.existsById(1L)).thenReturn(true);

        authUserService.eliminar(1L);

        verify(authUserRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(authUserRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authUserService.eliminar(99L)
        );

        assertEquals("No existe un usuario auth con ID: 99", exception.getMessage());
        verify(authUserRepository, never()).deleteById(anyLong());
    }

    @Test
    void getRole_deberiaRetornarRolDelUsuario() {
        AuthUser user = crearUsuario();
        user.setRol("ADMIN");

        when(authUserRepository.findByEmail("admin@gpustore.cl")).thenReturn(user);

        String resultado = authUserService.getRole("admin@gpustore.cl");

        assertEquals("ADMIN", resultado);
    }

    private AuthUser crearUsuario() {
        return new AuthUser(
                1L,
                "admin",
                "admin@gpustore.cl",
                "123456",
                "ADMIN"
        );
    }
}