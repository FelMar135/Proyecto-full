package com.example.producto_service.controller;

import com.example.producto_service.assembler.CategoriaModelAssembler;
import com.example.producto_service.dto.CategoriaDTO;
import com.example.producto_service.model.Categoria;
import com.example.producto_service.service.CategoriaService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final CategoriaModelAssembler categoriaModelAssembler;

    public CategoriaController(
            CategoriaService categoriaService,
            CategoriaModelAssembler categoriaModelAssembler
    ) {
        this.categoriaService = categoriaService;
        this.categoriaModelAssembler = categoriaModelAssembler;
    }

    @PostMapping
    public ResponseEntity<EntityModel<CategoriaDTO>> crearCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        Categoria nueva = categoriaService.guardar(categoriaDTO.toModel());
        CategoriaDTO respuesta = CategoriaDTO.fromModel(nueva);

        return ResponseEntity.ok(categoriaModelAssembler.toModel(respuesta));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CategoriaDTO>>> listarCategorias() {
        List<EntityModel<CategoriaDTO>> categorias = categoriaService.listar()
                .stream()
                .map(CategoriaDTO::fromModel)
                .map(categoriaModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<CategoriaDTO>> respuesta = CollectionModel.of(
                categorias,
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaDTO>> buscarCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.buscarPorId(id);
        CategoriaDTO respuesta = CategoriaDTO.fromModel(categoria);

        return ResponseEntity.ok(categoriaModelAssembler.toModel(respuesta));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.existePorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaDTO>> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody CategoriaDTO categoriaDTO
    ) {
        Categoria actualizada = categoriaService.actualizar(id, categoriaDTO.toModel());
        CategoriaDTO respuesta = CategoriaDTO.fromModel(actualizada);

        return ResponseEntity.ok(categoriaModelAssembler.toModel(respuesta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}