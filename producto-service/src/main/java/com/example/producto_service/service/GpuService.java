package com.example.producto_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.producto_service.model.Gpu;
import com.example.producto_service.repository.GpuRepository;

@Service
public class GpuService {

    private static final Logger logger = LoggerFactory.getLogger(GpuService.class);

    private final GpuRepository gpuRepository;

    public GpuService(GpuRepository gpuRepository) {
        this.gpuRepository = gpuRepository;
    }

    public Gpu guardar(Gpu gpu) {
        logger.info("Guardando GPU: {}", gpu.getNombre());
        return gpuRepository.save(gpu);
    }

    public List<Gpu> listar() {
        logger.info("Listando todas las GPUs");
        return gpuRepository.findAll();
    }

    public Gpu buscarPorId(Long id) {
        logger.info("Buscando GPU con ID: {}", id);
        return gpuRepository.findById(id).orElse(null);
    }

    public boolean existePorId(Long id) {
        logger.info("Verificando existencia de GPU con ID: {}", id);
        return gpuRepository.existsById(id);
    }

    public Gpu actualizar(Long id, Gpu gpuActualizada) {
        logger.info("Actualizando GPU con ID: {}", id);

        Gpu gpu = gpuRepository.findById(id).orElse(null);

        if (gpu == null) {
            logger.warn("No se encontró GPU con ID: {}", id);
            return null;
        }

        gpu.setNombre(gpuActualizada.getNombre());
        gpu.setMarca(gpuActualizada.getMarca());
        gpu.setModelo(gpuActualizada.getModelo());
        gpu.setVram(gpuActualizada.getVram());
        gpu.setPrecio(gpuActualizada.getPrecio());
        gpu.setEstado(gpuActualizada.getEstado());
        gpu.setCategoria(gpuActualizada.getCategoria());

        logger.info("GPU actualizada correctamente con ID: {}", id);
        return gpuRepository.save(gpu);
    }

    public boolean eliminar(Long id) {
        logger.warn("Intentando eliminar GPU con ID: {}", id);

        if (!gpuRepository.existsById(id)) {
            logger.error("No se pudo eliminar. GPU no encontrada con ID: {}", id);
            return false;
        }

        gpuRepository.deleteById(id);
        logger.info("GPU eliminada correctamente con ID: {}", id);
        return true;
    }
}