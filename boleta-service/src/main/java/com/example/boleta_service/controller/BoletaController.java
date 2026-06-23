package com.example.boleta_service.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.boleta_service.assembler.BoletaModelAssembler;
import com.example.boleta_service.dto.BoletaDTO;
import com.example.boleta_service.model.Boleta;
import com.example.boleta_service.service.BoletaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Boletas", description = "Operaciones relacionadas con boletas")
@RestController
@RequestMapping("/boletas")
public class BoletaController {

    
    private final BoletaService boletaService;

    private final BoletaModelAssembler assembler;

    public BoletaController(
        BoletaService boletaService,
        BoletaModelAssembler assembler) {
            this.boletaService = boletaService;
            this.assembler = assembler;
}

    @Operation(summary = "Crear una nueva boleta")
    @PostMapping
    public ResponseEntity<?> crearBoleta(
        @Valid @RequestBody BoletaDTO boletaDTO) {

        try {
            Boleta nueva = boletaService.guardar(boletaDTO.toModel());
            return ResponseEntity.ok(BoletaDTO.fromModel(nueva));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Listar todas las boletas")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Boleta>>> listarBoletas() {   

    List<EntityModel<Boleta>> boletas = boletaService.listar()
            .stream()
            .map(assembler::toModel)
            .toList();

    return ResponseEntity.ok(CollectionModel.of(boletas));
    }

    @Operation(summary = "Buscar una boleta por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Boleta>> buscarBoletaPorId(@PathVariable Long id) {

    Boleta boleta = boletaService.buscarPorId(id);

    if (boleta == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(assembler.toModel(boleta));
    }

    @Operation(summary = "Verificar si una boleta existe por su ID")
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeBoleta(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.existePorId(id));
    }

    @Operation(summary = "Actualizar una boleta")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarBoleta(
        @PathVariable Long id,
        @Valid @RequestBody BoletaDTO boletaDTO) {

        Boleta actualizada =
                boletaService.actualizar(id, boletaDTO.toModel());

        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                BoletaDTO.fromModel(actualizada));
    }

    @Operation(summary = "Eliminar una boleta")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBoleta(@PathVariable Long id) {

        boolean eliminada =
                boletaService.eliminar(id);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar boletas por ID de usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<Boleta>>> buscarPorUsuario(
        @PathVariable Long usuarioId) {

    List<EntityModel<Boleta>> boletas =
            boletaService.buscarPorUsuarioId(usuarioId)
                    .stream()
                    .map(assembler::toModel)
                    .toList();

    return ResponseEntity.ok(
            CollectionModel.of(boletas));
    }

    @Operation(summary = "Buscar boletas por ID de orden")
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<CollectionModel<EntityModel<Boleta>>> buscarPorOrden(
            @PathVariable Long ordenId) {

        List<EntityModel<Boleta>> boletas =
                boletaService.buscarPorOrdenId(ordenId)
                        .stream()
                        .map(assembler::toModel)
                        .toList();

        return ResponseEntity.ok(
                CollectionModel.of(boletas));
    }

    @Operation(summary = "Calcular el total comprado por un usuario")
    @GetMapping("/usuario/{usuarioId}/total")
    public ResponseEntity<Double> totalComprado(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                boletaService.totalCompradoPorUsuario(usuarioId));
    }
}