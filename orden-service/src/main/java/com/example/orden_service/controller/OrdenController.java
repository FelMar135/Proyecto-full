package com.example.orden_service.controller;

import com.example.orden_service.assembler.OrdenModelAssembler;
import com.example.orden_service.dto.OrdenDTO;
import com.example.orden_service.model.Orden;
import com.example.orden_service.service.OrdenService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {

    private final OrdenService ordenService;
    private final OrdenModelAssembler ordenModelAssembler;

    public OrdenController(OrdenService ordenService, OrdenModelAssembler ordenModelAssembler) {
        this.ordenService = ordenService;
        this.ordenModelAssembler = ordenModelAssembler;
    }

    @PostMapping
    public ResponseEntity<EntityModel<OrdenDTO>> crearOrden(@Valid @RequestBody OrdenDTO ordenDTO) {
        Orden nuevaOrden = ordenService.guardar(ordenDTO.toModel());
        OrdenDTO respuesta = OrdenDTO.fromModel(nuevaOrden);

        return ResponseEntity.ok(ordenModelAssembler.toModel(respuesta));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<OrdenDTO>>> listarOrdenes() {
        List<EntityModel<OrdenDTO>> ordenes = ordenService.listar()
                .stream()
                .map(OrdenDTO::fromModel)
                .map(ordenModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<OrdenDTO>> respuesta = CollectionModel.of(
                ordenes,
                linkTo(methodOn(OrdenController.class).listarOrdenes()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OrdenDTO>> buscarOrdenPorId(@PathVariable Long id) {
        Orden orden = ordenService.buscarPorId(id);
        OrdenDTO respuesta = OrdenDTO.fromModel(orden);

        return ResponseEntity.ok(ordenModelAssembler.toModel(respuesta));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeOrden(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.existePorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<OrdenDTO>> actualizarOrden(
            @PathVariable Long id,
            @Valid @RequestBody OrdenDTO ordenDTO
    ) {
        Orden ordenActualizada = ordenService.actualizar(id, ordenDTO.toModel());
        OrdenDTO respuesta = OrdenDTO.fromModel(ordenActualizada);

        return ResponseEntity.ok(ordenModelAssembler.toModel(respuesta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {
        ordenService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<OrdenDTO>>> buscarPorUsuario(@PathVariable Long usuarioId) {
        List<EntityModel<OrdenDTO>> ordenes = ordenService.buscarPorUsuarioId(usuarioId)
                .stream()
                .map(OrdenDTO::fromModel)
                .map(ordenModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<OrdenDTO>> respuesta = CollectionModel.of(
                ordenes,
                linkTo(methodOn(OrdenController.class).buscarPorUsuario(usuarioId)).withSelfRel(),
                linkTo(methodOn(OrdenController.class).listarOrdenes()).withRel("ordenes")
        );

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/total-ventas")
    public ResponseEntity<Double> totalVentas() {
        return ResponseEntity.ok(ordenService.totalVentas());
    }
}