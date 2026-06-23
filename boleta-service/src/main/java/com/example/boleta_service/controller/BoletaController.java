package com.example.boleta_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public BoletaController(BoletaService boletaService) {
        this.boletaService = boletaService;
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
    public ResponseEntity<List<BoletaDTO>> listarBoletas() {

        List<Boleta> boletas = boletaService.listar();

        List<BoletaDTO> dtos = boletas.stream()
                .map(BoletaDTO::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Buscar una boleta por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<BoletaDTO> buscarBoletaPorId(@PathVariable Long id) {

        Boleta boleta = boletaService.buscarPorId(id);

        if (boleta == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(BoletaDTO.fromModel(boleta));
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
    public ResponseEntity<List<BoletaDTO>> buscarPorUsuario(
            @PathVariable Long usuarioId) {

        List<Boleta> boletas =
                boletaService.buscarPorUsuarioId(usuarioId);

        List<BoletaDTO> dtos = boletas.stream()
                .map(BoletaDTO::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Buscar boletas por ID de orden")
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<BoletaDTO>> buscarPorOrden(
            @PathVariable Long ordenId) {

        List<Boleta> boletas =
                boletaService.buscarPorOrdenId(ordenId);

        List<BoletaDTO> dtos = boletas.stream()
                .map(BoletaDTO::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Calcular el total comprado por un usuario")
    @GetMapping("/usuario/{usuarioId}/total")
    public ResponseEntity<Double> totalComprado(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                boletaService.totalCompradoPorUsuario(usuarioId));
    }
}