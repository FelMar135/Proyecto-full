package com.example.producto_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.producto_service.dto.CategoriaDTO;
import com.example.producto_service.model.Categoria;
import com.example.producto_service.service.CategoriaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> crearCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        Categoria nueva = categoriaService.guardar(categoriaDTO.toModel());
        return ResponseEntity.ok(CategoriaDTO.fromModel(nueva));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
        List<Categoria> categorias = categoriaService.listar();

        List<CategoriaDTO> dtos = categorias.stream()
                .map(CategoriaDTO::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.buscarPorId(id);

        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(CategoriaDTO.fromModel(categoria));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.existePorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(@PathVariable Long id,
                                                            @RequestBody CategoriaDTO categoriaDTO) {
        Categoria actualizada = categoriaService.actualizar(id, categoriaDTO.toModel());

        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(CategoriaDTO.fromModel(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        boolean eliminada = categoriaService.eliminar(id);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}