package com.example.producto_service.service;

import com.example.producto_service.exception.BadRequestException;
import com.example.producto_service.exception.ResourceNotFoundException;
import com.example.producto_service.model.Categoria;
import com.example.producto_service.repository.CategoriaRepository;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void guardar_conDatosValidos_deberiaGuardarCategoria() {
        Categoria categoria = crearCategoria();

        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria resultado = categoriaService.guardar(categoria);

        assertNotNull(resultado);
        assertEquals("Tarjetas gráficas", resultado.getNombre());

        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void guardar_conCategoriaNula_deberiaLanzarBadRequestException() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> categoriaService.guardar(null)
        );

        assertEquals("La categoría no puede ser nula", exception.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void guardar_sinNombre_deberiaLanzarBadRequestException() {
        Categoria categoria = crearCategoria();
        categoria.setNombre("");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> categoriaService.guardar(categoria)
        );

        assertEquals("El nombre de la categoría es obligatorio", exception.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void guardar_sinDescripcion_deberiaLanzarBadRequestException() {
        Categoria categoria = crearCategoria();
        categoria.setDescripcion("");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> categoriaService.guardar(categoria)
        );

        assertEquals("La descripción de la categoría es obligatoria", exception.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void listar_deberiaRetornarCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(crearCategoria()));

        List<Categoria> resultado = categoriaService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Tarjetas gráficas", resultado.get(0).getNombre());

        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarCategoria() {
        Categoria categoria = crearCategoria();

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.buscarPorId(99L)
        );

        assertEquals("No existe una categoría con ID: 99", exception.getMessage());
    }

    @Test
    void existePorId_deberiaRetornarTrue() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = categoriaService.existePorId(1L);

        assertTrue(resultado);
        verify(categoriaRepository, times(1)).existsById(1L);
    }

    @Test
    void actualizar_cuandoExiste_deberiaActualizarCategoria() {
        Categoria existente = crearCategoria();
        Categoria actualizada = new Categoria(
                1L,
                "Componentes",
                "Componentes de computador",
                null
        );

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Categoria resultado = categoriaService.actualizar(1L, actualizada);

        assertEquals("Componentes", resultado.getNombre());
        assertEquals("Componentes de computador", resultado.getDescripcion());

        verify(categoriaRepository, times(1)).save(existente);
    }

    @Test
    void actualizar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.actualizar(99L, crearCategoria())
        );

        assertEquals("No existe una categoría con ID: 99", exception.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminarCategoria() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        categoriaService.eliminar(1L);

        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.eliminar(99L)
        );

        assertEquals("No existe una categoría con ID: 99", exception.getMessage());
        verify(categoriaRepository, never()).deleteById(anyLong());
    }

    private Categoria crearCategoria() {
        return new Categoria(
                1L,
                "Tarjetas gráficas",
                "GPUs para computadores gamer",
                null
        );
    }
}