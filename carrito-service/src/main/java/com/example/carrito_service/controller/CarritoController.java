package com.example.carrito_service.controller;

import com.example.carrito_service.model.Carrito;
import com.example.carrito_service.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/carritos")
@Tag(name = "Carritos", description = "Operaciones relacionadas con los carritos de compra")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los carritos")
    public ResponseEntity<CollectionModel<EntityModel<Carrito>>> obtenerTodos() {

        List<EntityModel<Carrito>> carritos = carritoService.listar().stream()
                .map(carrito -> EntityModel.of(carrito,
                        linkTo(methodOn(CarritoController.class)
                                .obtenerPorId(carrito.getId()))
                                .withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(carritos,
                        linkTo(methodOn(CarritoController.class)
                                .obtenerTodos())
                                .withSelfRel())
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener carrito por ID")
    public ResponseEntity<EntityModel<Carrito>> obtenerPorId(@PathVariable Long id) {

        Carrito carrito = carritoService.buscarPorId(id);

        EntityModel<Carrito> model = EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class)
                        .obtenerPorId(id))
                        .withSelfRel(),
                linkTo(methodOn(CarritoController.class)
                        .obtenerTodos())
                        .withRel("carritos"));

        return ResponseEntity.ok(model);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo carrito")
    public ResponseEntity<EntityModel<Carrito>> crear(@RequestBody Carrito carrito) {

        Carrito nuevoCarrito = carritoService.guardar(carrito);

        EntityModel<Carrito> model = EntityModel.of(nuevoCarrito,
                linkTo(methodOn(CarritoController.class)
                        .obtenerPorId(nuevoCarrito.getId()))
                        .withSelfRel());

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un carrito")
    public ResponseEntity<EntityModel<Carrito>> actualizar(
            @PathVariable Long id,
            @RequestBody Carrito carrito) {

        Carrito actualizado = carritoService.actualizar(id, carrito);

        EntityModel<Carrito> model = EntityModel.of(actualizado,
                linkTo(methodOn(CarritoController.class)
                        .obtenerPorId(id))
                        .withSelfRel());

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un carrito")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        carritoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Verificar si un carrito existe")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id) {

        return ResponseEntity.ok(carritoService.existePorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener carritos por usuario")
    public ResponseEntity<CollectionModel<EntityModel<Carrito>>> obtenerPorUsuario(
            @PathVariable Long usuarioId) {

        List<EntityModel<Carrito>> carritos = carritoService.buscarPorUsuarioId(usuarioId)
                .stream()
                .map(carrito -> EntityModel.of(carrito,
                        linkTo(methodOn(CarritoController.class)
                                .obtenerPorId(carrito.getId()))
                                .withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(carritos,
                        linkTo(methodOn(CarritoController.class)
                                .obtenerPorUsuario(usuarioId))
                                .withSelfRel())
        );
    }

    @GetMapping("/usuario/{usuarioId}/total")
    @Operation(summary = "Obtener total de productos en los carritos de un usuario")
    public ResponseEntity<Integer> totalProductosPorUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                carritoService.totalProductosPorUsuario(usuarioId)
        );
    }
}