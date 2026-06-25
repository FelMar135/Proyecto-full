package com.example.producto_service.service;

import com.example.producto_service.exception.BadRequestException;
import com.example.producto_service.exception.ResourceNotFoundException;
import com.example.producto_service.model.Categoria;
import com.example.producto_service.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria guardar(Categoria categoria) {
        validarCategoria(categoria);
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una categoría con ID: " + id));
    }

    public boolean existePorId(Long id) {
        return categoriaRepository.existsById(id);
    }

    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una categoría con ID: " + id));

        validarCategoria(categoriaActualizada);

        categoria.setNombre(categoriaActualizada.getNombre());
        categoria.setDescripcion(categoriaActualizada.getDescripcion());

        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe una categoría con ID: " + id);
        }

        categoriaRepository.deleteById(id);
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new BadRequestException("La categoría no puede ser nula");
        }

        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new BadRequestException("El nombre de la categoría es obligatorio");
        }

        if (categoria.getDescripcion() == null || categoria.getDescripcion().isBlank()) {
            throw new BadRequestException("La descripción de la categoría es obligatoria");
        }
    }
}