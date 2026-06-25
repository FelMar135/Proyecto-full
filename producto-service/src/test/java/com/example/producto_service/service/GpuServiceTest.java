package com.example.producto_service.service;

import com.example.producto_service.exception.BadRequestException;
import com.example.producto_service.exception.ResourceNotFoundException;
import com.example.producto_service.model.Categoria;
import com.example.producto_service.model.Gpu;
import com.example.producto_service.repository.CategoriaRepository;
import com.example.producto_service.repository.GpuRepository;
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
class GpuServiceTest {

    @Mock
    private GpuRepository gpuRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private GpuService gpuService;

    @Test
    void guardar_conDatosValidos_deberiaGuardarGpu() {
        Categoria categoria = crearCategoria();
        Gpu gpu = crearGpu();

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(gpuRepository.save(any(Gpu.class))).thenReturn(gpu);

        Gpu resultado = gpuService.guardar(gpu);

        assertNotNull(resultado);
        assertEquals("RTX 4060", resultado.getNombre());
        assertEquals(1L, resultado.getCategoria().getId());

        verify(categoriaRepository, times(1)).findById(1L);
        verify(gpuRepository, times(1)).save(gpu);
    }

    @Test
    void guardar_conGpuNula_deberiaLanzarBadRequestException() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> gpuService.guardar(null)
        );

        assertEquals("La GPU no puede ser nula", exception.getMessage());
        verify(gpuRepository, never()).save(any());
    }

    @Test
    void guardar_sinNombre_deberiaLanzarBadRequestException() {
        Gpu gpu = crearGpu();
        gpu.setNombre("");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> gpuService.guardar(gpu)
        );

        assertEquals("El nombre de la GPU es obligatorio", exception.getMessage());
        verify(gpuRepository, never()).save(any());
    }

    @Test
    void guardar_conPrecioCero_deberiaLanzarBadRequestException() {
        Gpu gpu = crearGpu();
        gpu.setPrecio(0L);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> gpuService.guardar(gpu)
        );

        assertEquals("El precio debe ser mayor a 0", exception.getMessage());
        verify(gpuRepository, never()).save(any());
    }

    @Test
    void guardar_conStockNegativo_deberiaLanzarBadRequestException() {
        Gpu gpu = crearGpu();
        gpu.setStock(-1L);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> gpuService.guardar(gpu)
        );

        assertEquals("El stock no puede ser negativo", exception.getMessage());
        verify(gpuRepository, never()).save(any());
    }

    @Test
    void guardar_conCategoriaInexistente_deberiaLanzarResourceNotFoundException() {
        Gpu gpu = crearGpu();

        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> gpuService.guardar(gpu)
        );

        assertEquals("No existe una categoría con ID: 1", exception.getMessage());
        verify(gpuRepository, never()).save(any());
    }

    @Test
    void listar_deberiaRetornarListaDeGpus() {
        when(gpuRepository.findAll()).thenReturn(List.of(crearGpu()));

        List<Gpu> resultado = gpuService.listar();

        assertEquals(1, resultado.size());
        assertEquals("RTX 4060", resultado.get(0).getNombre());

        verify(gpuRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarGpu() {
        Gpu gpu = crearGpu();

        when(gpuRepository.findById(1L)).thenReturn(Optional.of(gpu));

        Gpu resultado = gpuService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("RTX 4060", resultado.getNombre());

        verify(gpuRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(gpuRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> gpuService.buscarPorId(99L)
        );

        assertEquals("No existe una GPU con ID: 99", exception.getMessage());
    }

    @Test
    void existePorId_deberiaRetornarTrue() {
        when(gpuRepository.existsById(1L)).thenReturn(true);

        boolean resultado = gpuService.existePorId(1L);

        assertTrue(resultado);
        verify(gpuRepository, times(1)).existsById(1L);
    }

    @Test
    void actualizar_cuandoExiste_deberiaActualizarGpu() {
        Categoria categoria = crearCategoria();
        Gpu gpuExistente = crearGpu();

        Gpu gpuActualizada = crearGpu();
        gpuActualizada.setNombre("RTX 4070");
        gpuActualizada.setPrecio(699990L);
        gpuActualizada.setStock(8L);

        when(gpuRepository.findById(1L)).thenReturn(Optional.of(gpuExistente));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(gpuRepository.save(any(Gpu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Gpu resultado = gpuService.actualizar(1L, gpuActualizada);

        assertEquals("RTX 4070", resultado.getNombre());
        assertEquals(699990L, resultado.getPrecio());
        assertEquals(8L, resultado.getStock());

        verify(gpuRepository, times(1)).findById(1L);
        verify(gpuRepository, times(1)).save(gpuExistente);
    }

    @Test
    void actualizar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(gpuRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> gpuService.actualizar(99L, crearGpu())
        );

        assertEquals("No existe una GPU con ID: 99", exception.getMessage());
        verify(gpuRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminarGpu() {
        when(gpuRepository.existsById(1L)).thenReturn(true);

        gpuService.eliminar(1L);

        verify(gpuRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(gpuRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> gpuService.eliminar(99L)
        );

        assertEquals("No existe una GPU con ID: 99", exception.getMessage());
        verify(gpuRepository, never()).deleteById(anyLong());
    }

    private Gpu crearGpu() {
        return new Gpu(
                1L,
                "RTX 4060",
                "NVIDIA",
                "4060",
                8,
                399990L,
                "DISPONIBLE",
                10L,
                crearCategoria()
        );
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