package com.example.producto_service.controller;

import com.example.producto_service.assembler.CategoriaModelAssembler;
import com.example.producto_service.dto.CategoriaDTO;
import com.example.producto_service.model.Categoria;
import com.example.producto_service.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Categorías", description = "Operaciones relacionadas con categorías")
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

    @Operation(summary = "Crear una nueva categoría")
    @PostMapping
    public ResponseEntity<EntityModel<CategoriaDTO>> crearCategoria(
            @RequestBody CategoriaDTO categoriaDTO
    ) {

        Categoria nueva = categoriaService.guardar(categoriaDTO.toModel());

        return ResponseEntity.ok(
                categoriaModelAssembler.toModel(CategoriaDTO.fromModel(nueva)));
    }

    @Operation(summary = "Listar todas las categorías")
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

    @Operation(summary = "Buscar una categoría por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaDTO>> buscarCategoriaPorId(
            @PathVariable Long id
    ) {

        Categoria categoria = categoriaService.buscarPorId(id);

        return ResponseEntity.ok(
                categoriaModelAssembler.toModel(CategoriaDTO.fromModel(categoria)));
    }

    @Operation(summary = "Verificar si una categoría existe")
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeCategoria(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(categoriaService.existePorId(id));
    }

    @Operation(summary = "Actualizar una categoría")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaDTO>> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody CategoriaDTO categoriaDTO
    ) {

        Categoria actualizada = categoriaService.actualizar(id, categoriaDTO.toModel());

        return ResponseEntity.ok(
                categoriaModelAssembler.toModel(CategoriaDTO.fromModel(actualizada)));
    }

    @Operation(summary = "Eliminar una categoría")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(
            @PathVariable Long id
    ) {

        categoriaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}