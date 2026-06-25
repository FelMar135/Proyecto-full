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

    private static final Logger logger = LoggerFactory.getLogger(GpuService.class);

    private final GpuRepository gpuRepository;
    private final CategoriaRepository categoriaRepository;

    public GpuService(GpuRepository gpuRepository, CategoriaRepository categoriaRepository) {
        this.gpuRepository = gpuRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Gpu guardar(Gpu gpu) {
    validarGpu(gpu);

    logger.info("Guardando GPU: {}", gpu.getNombre());

    Categoria categoria = obtenerCategoriaValida(gpu);

    gpu.setCategoria(categoria);

    return gpuRepository.save(gpu);
}

    public List<Gpu> listar() {
        logger.info("Listando todas las GPUs");
        return gpuRepository.findAll();
    }

    public Gpu buscarPorId(Long id) {
        logger.info("Buscando GPU con ID: {}", id);

        return gpuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una GPU con ID: " + id));
    }

    public boolean existePorId(Long id) {
        logger.info("Verificando existencia de GPU con ID: {}", id);
        return gpuRepository.existsById(id);
    }

    public Gpu actualizar(Long id, Gpu gpuActualizada) {
        logger.info("Actualizando GPU con ID: {}", id);

        Gpu gpu = gpuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una GPU con ID: " + id));

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
            throw new ResourceNotFoundException("No existe una GPU con ID: " + id);
        }

        gpuRepository.deleteById(id);

        logger.info("GPU eliminada correctamente con ID: {}", id);
    }

    private void validarGpu(Gpu gpu) {
        if (gpu == null) {
            throw new BadRequestException("La GPU no puede ser nula");
        }

        if (gpu.getNombre() == null || gpu.getNombre().isBlank()) {
            throw new BadRequestException("El nombre de la GPU es obligatorio");
        }

        if (gpu.getMarca() == null || gpu.getMarca().isBlank()) {
            throw new BadRequestException("La marca de la GPU es obligatoria");
        }

        if (gpu.getModelo() == null || gpu.getModelo().isBlank()) {
            throw new BadRequestException("El modelo de la GPU es obligatorio");
        }

        if (gpu.getVram() == null || gpu.getVram() <= 0) {
            throw new BadRequestException("La VRAM debe ser mayor a 0");
        }

        if (gpu.getPrecio() == null || gpu.getPrecio() <= 0) {
            throw new BadRequestException("El precio debe ser mayor a 0");
        }

        if (gpu.getStock() == null || gpu.getStock() < 0) {
            throw new BadRequestException("El stock no puede ser negativo");
        }

        if (gpu.getEstado() == null || gpu.getEstado().isBlank()) {
            throw new BadRequestException("El estado de la GPU es obligatorio");
        }
    }

    private Categoria obtenerCategoriaValida(Gpu gpu) {
        if (gpu.getCategoria() == null || gpu.getCategoria().getId() == null) {
            throw new BadRequestException("La categoría de la GPU es obligatoria");
        }

        Long categoriaId = gpu.getCategoria().getId();

        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una categoría con ID: " + categoriaId));
    }
}