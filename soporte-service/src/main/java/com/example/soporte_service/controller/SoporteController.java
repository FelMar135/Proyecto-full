package com.example.soporte_service.controller;

import com.example.soporte_service.assembler.SoporteModelAssembler;
import com.example.soporte_service.dto.SoporteDTO;
import com.example.soporte_service.service.SoporteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Soporte", description = "Operaciones relacionadas con los tickets de soporte")
@RestController
@RequestMapping("/soporte")
public class SoporteController {

    private final SoporteService soporteService;
    private final SoporteModelAssembler assembler;

    public SoporteController(SoporteService soporteService, SoporteModelAssembler assembler) {
        this.soporteService = soporteService;
        this.assembler = assembler;
    }

    @Operation(summary = "Listar todos los tickets de soporte")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<SoporteDTO>>> obtenerTodos() {
        List<EntityModel<SoporteDTO>> tickets = soporteService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(
                tickets,
                linkTo(methodOn(SoporteController.class).obtenerTodos()).withSelfRel()
        ));
    }

    @Operation(summary = "Buscar un ticket de soporte por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SoporteDTO>> obtenerPorId(@PathVariable Long id) {
        SoporteDTO soporte = soporteService.findById(id);
        return ResponseEntity.ok(assembler.toModel(soporte));
    }

    @Operation(summary = "Buscar tickets por ID de usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<SoporteDTO>>> obtenerPorUsuarioId(@PathVariable Long usuarioId) {
        List<EntityModel<SoporteDTO>> tickets = soporteService.findByUsuarioId(usuarioId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(
                tickets,
                linkTo(methodOn(SoporteController.class).obtenerPorUsuarioId(usuarioId)).withSelfRel()
        ));
    }

    @Operation(summary = "Buscar tickets por ID de orden")
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<CollectionModel<EntityModel<SoporteDTO>>> obtenerPorOrdenId(@PathVariable Long ordenId) {
        List<EntityModel<SoporteDTO>> tickets = soporteService.findByOrdenId(ordenId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(
                tickets,
                linkTo(methodOn(SoporteController.class).obtenerPorOrdenId(ordenId)).withSelfRel()
        ));
    }

    @Operation(summary = "Verificar si un ticket existe")
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id) {
        return ResponseEntity.ok(soporteService.existsById(id));
    }

    @Operation(summary = "Crear un nuevo ticket de soporte")
    @PostMapping
    public ResponseEntity<EntityModel<SoporteDTO>> crearTicket(@RequestBody SoporteDTO dto) {
        SoporteDTO nuevoTicket = soporteService.save(dto);
        EntityModel<SoporteDTO> entityModel = assembler.toModel(nuevoTicket);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @Operation(summary = "Actualizar un ticket de soporte")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<SoporteDTO>> actualizarTicket(
            @PathVariable Long id,
            @RequestBody SoporteDTO dto
    ) {
        SoporteDTO ticketActualizado = soporteService.update(id, dto);
        return ResponseEntity.ok(assembler.toModel(ticketActualizado));
    }

    @Operation(summary = "Eliminar un ticket de soporte")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTicket(@PathVariable Long id) {
        soporteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}