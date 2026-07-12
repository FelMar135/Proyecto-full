package com.example.producto_service.service;

import com.example.producto_service.exception.BadRequestException;
import com.example.producto_service.exception.ResourceNotFoundException;
import com.example.producto_service.model.Categoria;
import com.example.producto_service.model.Gpu;
import com.example.producto_service.repository.CategoriaRepository;
import com.example.producto_service.repository.GpuRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GpuService {

    private static final Logger logger =
            LoggerFactory.getLogger(GpuService.class);

    private final GpuRepository gpuRepository;
    private final CategoriaRepository categoriaRepository;

    public GpuService(
            GpuRepository gpuRepository,
            CategoriaRepository categoriaRepository) {

        this.gpuRepository = gpuRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Gpu guardar(Gpu gpu) {

        logger.info("Intentando guardar GPU: {}", gpu.getNombre());

        validarGpu(gpu);
        Categoria categoria = obtenerCategoriaValida(gpu);
        gpu.setCategoria(categoria);
        Gpu gpuGuardada = gpuRepository.save(gpu);

        logger.info("GPU guardada correctamente con ID: {}", gpuGuardada.getId());
        return gpuGuardada;
    }

    public List<Gpu> listar() {

        logger.info("Listando todas las GPUs");
        return gpuRepository.findAll();
    }

    public Gpu buscarPorId(Long id) {

        logger.info("Buscando GPU con ID: {}", id);
        return gpuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la GPU con ID: {}", id);
                    return new ResourceNotFoundException("No existe una GPU con ID: " + id);
                });
    }

    public boolean existePorId(Long id) {

        logger.info("Verificando existencia de GPU con ID: {}", id);
        return gpuRepository.existsById(id);
    }

    public Gpu actualizar(Long id, Gpu gpuActualizada) {
        logger.info("Intentando actualizar GPU con ID: {}", id);
        Gpu gpu = gpuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la GPU con ID: {}", id);
                    return new ResourceNotFoundException("No existe una GPU con ID: " + id);
                });

        validarGpu(gpuActualizada);

        Categoria categoria = obtenerCategoriaValida(gpuActualizada);

        gpu.setNombre(gpuActualizada.getNombre());
        gpu.setMarca(gpuActualizada.getMarca());
        gpu.setModelo(gpuActualizada.getModelo());
        gpu.setVram(gpuActualizada.getVram());
        gpu.setPrecio(gpuActualizada.getPrecio());
        gpu.setEstado(gpuActualizada.getEstado());
        gpu.setStock(gpuActualizada.getStock());
        gpu.setCategoria(categoria);

        logger.info("GPU actualizada correctamente con ID: {}", id);
        return gpuRepository.save(gpu);
    }

    public void eliminar(Long id) {

        logger.warn("Intentando eliminar GPU con ID: {}", id);

        if (!gpuRepository.existsById(id)) {
            logger.error("No se encontró la GPU con ID: {}", id);
            throw new ResourceNotFoundException("No existe una GPU con ID: " + id);
        }

        gpuRepository.deleteById(id);

        logger.info("GPU eliminada correctamente con ID: {}", id);
    }

    private void validarGpu(Gpu gpu) {

        if (gpu == null) {
            logger.error("La GPU recibida es nula");
            throw new BadRequestException("La GPU no puede ser nula");
        }

        if (gpu.getNombre() == null || gpu.getNombre().isBlank()) {
            logger.error("El nombre de la GPU es obligatorio");
            throw new BadRequestException("El nombre de la GPU es obligatorio");
        }

        if (gpu.getMarca() == null || gpu.getMarca().isBlank()) {
            logger.error("La marca de la GPU es obligatoria");
            throw new BadRequestException("La marca de la GPU es obligatoria");
        }

        if (gpu.getModelo() == null || gpu.getModelo().isBlank()) {
            logger.error("El modelo de la GPU es obligatorio");
            throw new BadRequestException("El modelo de la GPU es obligatorio");
        }

        if (gpu.getVram() == null || gpu.getVram() <= 0) {
            logger.error("La VRAM debe ser mayor a 0");
            throw new BadRequestException("La VRAM debe ser mayor a 0");
        }

        if (gpu.getPrecio() == null || gpu.getPrecio() <= 0) {
            logger.error("El precio debe ser mayor a 0");
            throw new BadRequestException("El precio debe ser mayor a 0");
        }

        if (gpu.getStock() == null || gpu.getStock() < 0) {
            logger.error("El stock no puede ser negativo");
            throw new BadRequestException("El stock no puede ser negativo");
        }

        if (gpu.getEstado() == null || gpu.getEstado().isBlank()) {
            logger.error("El estado de la GPU es obligatorio");
            throw new BadRequestException("El estado de la GPU es obligatorio");
        }
    }

    private Categoria obtenerCategoriaValida(Gpu gpu) {

        if (gpu.getCategoria() == null || gpu.getCategoria().getId() == null) {
            logger.error("La categoría de la GPU es obligatoria");
            throw new BadRequestException("La categoría de la GPU es obligatoria");
        }

        Long categoriaId = gpu.getCategoria().getId();

        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> {
                    logger.error("No existe la categoría con ID: {}", categoriaId);
                    return new ResourceNotFoundException(
                            "No existe una categoría con ID: " + categoriaId);
                });
    }
}