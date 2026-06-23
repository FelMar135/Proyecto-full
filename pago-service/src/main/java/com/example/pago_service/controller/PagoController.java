package com.example.pago_service.controller;

import com.example.pago_service.dto.PagoDTO;
import com.example.pago_service.service.PagoService;
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
public class PagoController {

    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PagoDTO>>> obtenerTodos() {
        log.info("GET /pagos - Solicitud para obtener todos los pagos");

        List<EntityModel<PagoDTO>> pagos = pagoService.obtenerTodos()
                .stream()
                .map(this::agregarLinks)
                .toList();

        CollectionModel<EntityModel<PagoDTO>> collectionModel = CollectionModel.of(
                pagos,
                linkTo(methodOn(PagoController.class).obtenerTodos()).withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PagoDTO>> obtenerPorId(@PathVariable Long id) {
        log.info("GET /pagos/{} - Solicitud para obtener pago por ID", id);

        PagoDTO pagoDTO = pagoService.obtenerPorId(id);

        return ResponseEntity.ok(agregarLinks(pagoDTO));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id) {
        log.info("GET /pagos/{}/exists - Solicitud para validar existencia de pago", id);

        boolean existe = pagoService.existePorId(id);

        return ResponseEntity.ok(existe);
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<CollectionModel<EntityModel<PagoDTO>>> obtenerPorOrdenId(@PathVariable Long ordenId) {
        log.info("GET /pagos/orden/{} - Solicitud para obtener pagos por orden", ordenId);

        List<EntityModel<PagoDTO>> pagos = pagoService.obtenerPorOrdenId(ordenId)
                .stream()
                .map(this::agregarLinks)
                .toList();

        CollectionModel<EntityModel<PagoDTO>> collectionModel = CollectionModel.of(
                pagos,
                linkTo(methodOn(PagoController.class).obtenerPorOrdenId(ordenId)).withSelfRel(),
                linkTo(methodOn(PagoController.class).obtenerTodos()).withRel("todos-los-pagos")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping
    public ResponseEntity<PagoDTO> crear(@Valid @RequestBody PagoDTO pagoDTO) {
        log.info("POST /pagos - Solicitud para crear pago");

        PagoDTO pagoCreado = pagoService.crear(pagoDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagoCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PagoDTO pagoDTO
    ) {
        log.info("PUT /pagos/{} - Solicitud para actualizar pago", id);

        PagoDTO pagoActualizado = pagoService.actualizar(id, pagoDTO);

        return ResponseEntity.ok(pagoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /pagos/{} - Solicitud para eliminar pago", id);

        pagoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<PagoDTO> agregarLinks(PagoDTO pagoDTO) {
        return EntityModel.of(
                pagoDTO,
                linkTo(methodOn(PagoController.class).obtenerPorId(pagoDTO.getId())).withSelfRel(),
                linkTo(methodOn(PagoController.class).obtenerTodos()).withRel("todos-los-pagos"),
                linkTo(methodOn(PagoController.class).obtenerPorOrdenId(pagoDTO.getOrdenId())).withRel("pagos-por-orden"),
                linkTo(methodOn(PagoController.class).existePorId(pagoDTO.getId())).withRel("existe")
        );
    }
}