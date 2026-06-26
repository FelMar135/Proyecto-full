package com.example.pago_service.controller;

import com.example.pago_service.assembler.PagoModelAssembler;
import com.example.pago_service.dto.PagoDTO;
import com.example.pago_service.service.PagoService;
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
@RequestMapping("/pagos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pagos", description = "Operaciones CRUD para gestionar pagos de órdenes")
public class PagoController {

    private final PagoService pagoService;
    private final PagoModelAssembler pagoModelAssembler;

    @GetMapping
    @Operation(summary = "Obtener todos los pagos")
    public ResponseEntity<CollectionModel<EntityModel<PagoDTO>>> obtenerTodos() {
        log.info("GET /pagos - Obteniendo todos los pagos");

        List<EntityModel<PagoDTO>> pagos = pagoService.obtenerTodos()
                .stream()
                .map(pagoModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<PagoDTO>> response = CollectionModel.of(
                pagos,
                linkTo(methodOn(PagoController.class).obtenerTodos()).withSelfRel()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pago por ID")
    public ResponseEntity<EntityModel<PagoDTO>> obtenerPorId(@PathVariable Long id) {
        log.info("GET /pagos/{} - Obteniendo pago por ID", id);

        PagoDTO pagoDTO = pagoService.obtenerPorId(id);

        return ResponseEntity.ok(pagoModelAssembler.toModel(pagoDTO));
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Validar si existe un pago por ID")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id) {
        log.info("GET /pagos/{}/exists - Validando existencia del pago", id);

        boolean existe = pagoService.existePorId(id);

        return ResponseEntity.ok(existe);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener pagos filtrados por estado")
    public ResponseEntity<CollectionModel<EntityModel<PagoDTO>>> obtenerPorEstado(@PathVariable String estado) {
        log.info("GET /pagos/estado/{} - Obteniendo pagos por estado", estado);

        List<EntityModel<PagoDTO>> pagos = pagoService.buscarPorEstado(estado)
                .stream()
                .map(pagoModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<PagoDTO>> response = CollectionModel.of(
                pagos,
                linkTo(methodOn(PagoController.class).obtenerPorEstado(estado)).withSelfRel(),
                linkTo(methodOn(PagoController.class).obtenerTodos()).withRel("todos-los-pagos")
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orden/{ordenId}")
    @Operation(summary = "Obtener pagos asociados a una orden")
    public ResponseEntity<CollectionModel<EntityModel<PagoDTO>>> obtenerPorOrdenId(@PathVariable Long ordenId) {
        log.info("GET /pagos/orden/{} - Obteniendo pagos por orden", ordenId);

        List<EntityModel<PagoDTO>> pagos = pagoService.obtenerPorOrdenId(ordenId)
                .stream()
                .map(pagoModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<PagoDTO>> response = CollectionModel.of(
                pagos,
                linkTo(methodOn(PagoController.class).obtenerPorOrdenId(ordenId)).withSelfRel(),
                linkTo(methodOn(PagoController.class).obtenerTodos()).withRel("todos-los-pagos")
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pago")
    public ResponseEntity<EntityModel<PagoDTO>> crear(@Valid @RequestBody PagoDTO pagoDTO) {
        log.info("POST /pagos - Creando nuevo pago");

        PagoDTO pagoCreado = pagoService.crear(pagoDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pagoModelAssembler.toModel(pagoCreado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un pago existente")
    public ResponseEntity<EntityModel<PagoDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PagoDTO pagoDTO
    ) {
        log.info("PUT /pagos/{} - Actualizando pago", id);

        PagoDTO pagoActualizado = pagoService.actualizar(id, pagoDTO);

        return ResponseEntity.ok(pagoModelAssembler.toModel(pagoActualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pago por ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /pagos/{} - Eliminando pago", id);

        pagoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}