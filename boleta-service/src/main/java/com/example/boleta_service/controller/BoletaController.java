package com.example.boleta_service.controller;

import com.example.boleta_service.assembler.BoletaModelAssembler;
import com.example.boleta_service.dto.BoletaDTO;
import com.example.boleta_service.model.Boleta;
import com.example.boleta_service.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Boletas", description = "Operaciones relacionadas con boletas")
@RestController
@RequestMapping("/boletas")
public class BoletaController {

    private final BoletaService boletaService;
    private final BoletaModelAssembler assembler;

    public BoletaController(
            BoletaService boletaService,
            BoletaModelAssembler assembler
    ) {
        this.boletaService = boletaService;
        this.assembler = assembler;
    }

    @Operation(summary = "Crear una nueva boleta")
    @PostMapping
    public ResponseEntity<EntityModel<Boleta>> crearBoleta(
            @Valid @RequestBody BoletaDTO boletaDTO
    ) {
        Boleta nueva = boletaService.guardar(boletaDTO.toModel());
        return ResponseEntity.ok(assembler.toModel(nueva));
    }

    @Operation(summary = "Listar todas las boletas")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Boleta>>> listarBoletas() {
        List<EntityModel<Boleta>> boletas = boletaService.listar()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<Boleta>> respuesta = CollectionModel.of(
                boletas,
                linkTo(methodOn(BoletaController.class).listarBoletas()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Buscar una boleta por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Boleta>> buscarBoletaPorId(@PathVariable Long id) {
        Boleta boleta = boletaService.buscarPorId(id);
        return ResponseEntity.ok(assembler.toModel(boleta));
    }

    @Operation(summary = "Verificar si una boleta existe por su ID")
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeBoleta(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.existePorId(id));
    }

    @Operation(summary = "Actualizar una boleta")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Boleta>> actualizarBoleta(
            @PathVariable Long id,
            @Valid @RequestBody BoletaDTO boletaDTO
    ) {
        Boleta actualizada = boletaService.actualizar(id, boletaDTO.toModel());
        return ResponseEntity.ok(assembler.toModel(actualizada));
    }

    @Operation(summary = "Eliminar una boleta")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBoleta(@PathVariable Long id) {
        boletaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar boletas por ID de usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<Boleta>>> buscarPorUsuario(
            @PathVariable Long usuarioId
    ) {
        List<EntityModel<Boleta>> boletas = boletaService.buscarPorUsuarioId(usuarioId)
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<Boleta>> respuesta = CollectionModel.of(
                boletas,
                linkTo(methodOn(BoletaController.class).buscarPorUsuario(usuarioId)).withSelfRel(),
                linkTo(methodOn(BoletaController.class).listarBoletas()).withRel("boletas")
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Buscar boletas por ID de orden")
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<CollectionModel<EntityModel<Boleta>>> buscarPorOrden(
            @PathVariable Long ordenId
    ) {
        List<EntityModel<Boleta>> boletas = boletaService.buscarPorOrdenId(ordenId)
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<Boleta>> respuesta = CollectionModel.of(
                boletas,
                linkTo(methodOn(BoletaController.class).buscarPorOrden(ordenId)).withSelfRel(),
                linkTo(methodOn(BoletaController.class).listarBoletas()).withRel("boletas")
        );

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Calcular el total comprado por un usuario")
    @GetMapping("/usuario/{usuarioId}/total")
    public ResponseEntity<Double> totalComprado(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(boletaService.totalCompradoPorUsuario(usuarioId));
    }
}