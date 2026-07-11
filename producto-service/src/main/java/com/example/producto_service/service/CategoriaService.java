package com.example.producto_service.service;

import com.example.producto_service.exception.BadRequestException;
import com.example.producto_service.exception.ResourceNotFoundException;
import com.example.producto_service.model.Categoria;
import com.example.producto_service.repository.CategoriaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private static final Logger logger =
            LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria guardar(Categoria categoria) {

        logger.info("Intentando guardar categoría: {}", categoria.getNombre());

        validarCategoria(categoria);

        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        logger.info("Categoría guardada correctamente con ID: {}", categoriaGuardada.getId());

        return categoriaGuardada;
    }

    public List<Categoria> listar() {

        logger.info("Listando todas las categorías");

        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {

        logger.info("Buscando categoría con ID: {}", id);

        return categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la categoría con ID: {}", id);
                    return new ResourceNotFoundException("No existe una categoría con ID: " + id);
                });
    }

    public boolean existePorId(Long id) {

        logger.info("Verificando existencia de categoría con ID: {}", id);

        return categoriaRepository.existsById(id);
    }

    public Categoria actualizar(Long id, Categoria categoriaActualizada) {

        logger.info("Intentando actualizar categoría con ID: {}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró la categoría con ID: {}", id);
                    return new ResourceNotFoundException("No existe una categoría con ID: " + id);
                });

        validarCategoria(categoriaActualizada);

        categoria.setNombre(categoriaActualizada.getNombre());
        categoria.setDescripcion(categoriaActualizada.getDescripcion());

        logger.info("Categoría actualizada correctamente con ID: {}", id);

        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {

        logger.warn("Intentando eliminar categoría con ID: {}", id);

        if (!categoriaRepository.existsById(id)) {

            logger.error("No se encontró la categoría con ID: {}", id);

            throw new ResourceNotFoundException("No existe una categoría con ID: " + id);
        }

        categoriaRepository.deleteById(id);

        logger.info("Categoría eliminada correctamente con ID: {}", id);
    }

    private void validarCategoria(Categoria categoria) {

        if (categoria == null) {
            logger.error("La categoría recibida es nula");
            throw new BadRequestException("La categoría no puede ser nula");
        }

        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            logger.error("El nombre de la categoría es obligatorio");
            throw new BadRequestException("El nombre de la categoría es obligatorio");
        }

        if (categoria.getDescripcion() == null || categoria.getDescripcion().isBlank()) {
            logger.error("La descripción de la categoría es obligatoria");
            throw new BadRequestException("La descripción de la categoría es obligatoria");
        }
    }
}