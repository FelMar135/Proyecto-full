package com.example.soporte_service.controller;

import com.example.soporte_service.assembler.SoporteModelAssembler;
import com.example.soporte_service.dto.SoporteDTO;
import com.example.soporte_service.service.SoporteService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/soporte")
public class SoporteController {

    private final SoporteService soporteService;
    private final SoporteModelAssembler assembler;

    public SoporteController(SoporteService soporteService, SoporteModelAssembler assembler) {
        this.soporteService = soporteService;
        this.assembler = assembler;
    }

    // 1. OBTENER TODOS
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<SoporteDTO>>> obtenerTodos() {
        List<EntityModel<SoporteDTO>> tickets = soporteService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(tickets,
                linkTo(methodOn(SoporteController.class).obtenerTodos()).withSelfRel()));
    }

    // 2. OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SoporteDTO>> obtenerPorId(@PathVariable Long id) {
        SoporteDTO soporte = soporteService.findById(id);
        return ResponseEntity.ok(assembler.toModel(soporte));
    }

    // 3. OBTENER POR USUARIO ID
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<SoporteDTO>>> obtenerPorUsuarioId(@PathVariable Long usuarioId) {
        List<EntityModel<SoporteDTO>> tickets = soporteService.findByUsuarioId(usuarioId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(tickets,
                linkTo(methodOn(SoporteController.class).obtenerPorUsuarioId(usuarioId)).withSelfRel()));
    }

    // 4. OBTENER POR ORDEN ID
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<CollectionModel<EntityModel<SoporteDTO>>> obtenerPorOrdenId(@PathVariable Long ordenId) {
        List<EntityModel<SoporteDTO>> tickets = soporteService.findByOrdenId(ordenId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(tickets,
                linkTo(methodOn(SoporteController.class).obtenerPorOrdenId(ordenId)).withSelfRel()));
    }

    // 5. VERIFICAR SI EXISTE POR ID
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id) {
        // Llama al método existeById en tu servicio (o crealo si aún no lo tienes)
        return ResponseEntity.ok(soporteService.existsById(id));
    }

    // 6. CREAR (POST)
    @PostMapping
    public ResponseEntity<EntityModel<SoporteDTO>> crearTicket(@RequestBody SoporteDTO dto) {
        SoporteDTO nuevoTicket = soporteService.save(dto);
        EntityModel<SoporteDTO> entityModel = assembler.toModel(nuevoTicket);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // 7. ACTUALIZAR (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<SoporteDTO>> actualizarTicket(@PathVariable Long id, @RequestBody SoporteDTO dto) {
        SoporteDTO ticketActualizado = soporteService.update(id, dto);
        return ResponseEntity.ok(assembler.toModel(ticketActualizado));
    }

    // 8. ELIMINAR (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTicket(@PathVariable Long id) {
        soporteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}