package com.example.envio_service.controller;

import com.example.envio_service.assembler.EnvioModelAssembler;
import com.example.envio_service.dto.EnvioDTO;
import com.example.envio_service.service.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/envios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Envios", description = "Operaciones CRUD para gestionar envíos de órdenes")
public class EnvioController {

    private final EnvioService envioService;
    private final EnvioModelAssembler envioModelAssembler;

    @GetMapping
    @Operation(summary = "Obtener todos los envíos")
    public ResponseEntity<CollectionModel<EntityModel<EnvioDTO>>> obtenerTodos() {
        log.info("GET /envios - Obteniendo todos los envíos");

        List<EntityModel<EnvioDTO>> envios = envioService.obtenerTodos()
                .stream()
                .map(envioModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<EnvioDTO>> response = CollectionModel.of(
                envios,
                linkTo(methodOn(EnvioController.class).obtenerTodos()).withSelfRel()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un envío por ID")
    public ResponseEntity<EntityModel<EnvioDTO>> obtenerPorId(@PathVariable Long id) {
        log.info("GET /envios/{} - Obteniendo envío por ID", id);

        EnvioDTO envioDTO = envioService.obtenerPorId(id);

        return ResponseEntity.ok(envioModelAssembler.toModel(envioDTO));
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Validar si existe un envío por ID")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id) {
        log.info("GET /envios/{}/exists - Validando existencia del envío", id);

        boolean existe = envioService.existePorId(id);

        return ResponseEntity.ok(existe);
    }

    @GetMapping("/orden/{ordenId}")
    @Operation(summary = "Obtener envíos asociados a una orden")
    public ResponseEntity<CollectionModel<EntityModel<EnvioDTO>>> obtenerPorOrdenId(@PathVariable Long ordenId) {
        log.info("GET /envios/orden/{} - Obteniendo envíos por orden", ordenId);

        List<EntityModel<EnvioDTO>> envios = envioService.obtenerPorOrdenId(ordenId)
                .stream()
                .map(envioModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<EnvioDTO>> response = CollectionModel.of(
                envios,
                linkTo(methodOn(EnvioController.class).obtenerPorOrdenId(ordenId)).withSelfRel(),
                linkTo(methodOn(EnvioController.class).obtenerTodos()).withRel("todos-los-envios")
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener envíos por estado")
    public ResponseEntity<CollectionModel<EntityModel<EnvioDTO>>> obtenerPorEstado(@PathVariable String estado) {
        log.info("GET /envios/estado/{} - Obteniendo envíos por estado", estado);

        List<EntityModel<EnvioDTO>> envios = envioService.obtenerPorEstado(estado)
                .stream()
                .map(envioModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<EnvioDTO>> response = CollectionModel.of(
                envios,
                linkTo(methodOn(EnvioController.class).obtenerPorEstado(estado)).withSelfRel(),
                linkTo(methodOn(EnvioController.class).obtenerTodos()).withRel("todos-los-envios")
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ciudad/{ciudad}")
    @Operation(summary = "Obtener envíos por ciudad")
    public ResponseEntity<CollectionModel<EntityModel<EnvioDTO>>> obtenerPorCiudad(@PathVariable String ciudad) {
        log.info("GET /envios/ciudad/{} - Obteniendo envíos por ciudad", ciudad);

        List<EntityModel<EnvioDTO>> envios = envioService.obtenerPorCiudad(ciudad)
                .stream()
                .map(envioModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<EnvioDTO>> response = CollectionModel.of(
                envios,
                linkTo(methodOn(EnvioController.class).obtenerPorCiudad(ciudad)).withSelfRel(),
                linkTo(methodOn(EnvioController.class).obtenerTodos()).withRel("todos-los-envios")
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo envío")
    public ResponseEntity<EnvioDTO> crear(@Valid @RequestBody EnvioDTO envioDTO) {
        log.info("POST /envios - Creando nuevo envío");

        EnvioDTO envioCreado = envioService.crear(envioDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(envioCreado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un envío existente")
    public ResponseEntity<EnvioDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EnvioDTO envioDTO
    ) {
        log.info("PUT /envios/{} - Actualizando envío", id);

        EnvioDTO envioActualizado = envioService.actualizar(id, envioDTO);

        return ResponseEntity.ok(envioActualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un envío por ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /envios/{} - Eliminando envío", id);

        envioService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}