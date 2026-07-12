package com.example.user_service.service;

import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.model.Usuario;
import com.example.user_service.repository.UsuarioRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Faker faker;
    private Usuario usuario;

    @BeforeEach
    void setUp() {

        faker = new Faker(new Locale("es"));

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre(faker.name().firstName());
        usuario.setApellido(faker.name().lastName());
        usuario.setEmail(faker.internet().emailAddress());
        usuario.setTelefono(faker.phoneNumber().cellPhone());
        usuario.setDireccion(faker.address().fullAddress());
    }

    @Test
    void debeGuardarUsuario() {

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        Usuario resultado = usuarioService.guardar(usuario);

        assertNotNull(resultado);
        assertEquals(usuario.getId(), resultado.getId());

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void debeListarTodosLosUsuarios() {

        when(usuarioRepository.findAll())
                .thenReturn(Arrays.asList(usuario));

        List<Usuario> resultado = usuarioService.listar();

        assertEquals(1, resultado.size());

        verify(usuarioRepository).findAll();
    }

    @Test
    void debeBuscarUsuarioPorId() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void debeLanzarExcepcionSiNoExisteUsuario() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.buscarPorId(99L)
        );
    }

    @Test
    void debeVerificarExistenciaPorId() {

        when(usuarioRepository.existsById(1L))
                .thenReturn(true);

        assertTrue(usuarioService.existePorId(1L));
    }

    @Test
    void debeActualizarUsuario() {

        Usuario actualizado = new Usuario(
                1L,
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                faker.phoneNumber().cellPhone(),
                faker.address().fullAddress()
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado =
                usuarioService.actualizar(1L, actualizado);

        assertEquals(
                actualizado.getNombre(),
                resultado.getNombre()
        );

        assertEquals(
                actualizado.getEmail(),
                resultado.getEmail()
        );

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void debeLanzarExcepcionSiActualizaUsuarioInexistente() {

        when(usuarioRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.actualizar(999L, usuario)
        );
    }

    @Test
    void debeEliminarUsuario() {

        when(usuarioRepository.existsById(1L))
                .thenReturn(true);

        usuarioService.eliminar(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void debeLanzarExcepcionAlEliminarUsuarioInexistente() {

        when(usuarioRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.eliminar(99L)
        );

        verify(usuarioRepository, never())
                .deleteById(anyLong());
    }
}