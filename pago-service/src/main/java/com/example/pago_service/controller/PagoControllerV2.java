package com.example.pago_service.controller;

import com.example.pago_service.dto.PagoDTO;
import com.example.pago_service.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/pagos/v2")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pagos V2", description = "Versión 2 de la API de pagos con HATEOAS")
public class PagoControllerV2 {

    private final PagoService pagoService;

    @GetMapping
    @Operation(summary = "Listar todos los pagos V2")
    public CollectionModel<EntityModel<PagoDTO>> listarPagos() {
        log.info("GET /pagos/v2 - Listando pagos V2");

        List<EntityModel<PagoDTO>> pagos = pagoService.obtenerTodos()
                .stream()
                .map(pago -> EntityModel.of(
                        pago,
                        linkTo(methodOn(PagoControllerV2.class).obtenerPago(pago.getId())).withSelfRel(),
                        linkTo(methodOn(PagoControllerV2.class).listarPagos()).withRel("todos-los-pagos-v2"),
                        linkTo(methodOn(PagoControllerV2.class).obtenerPagosPorOrden(pago.getOrdenId())).withRel("pagos-por-orden-v2")
                ))
                .toList();

        return CollectionModel.of(
                pagos,
                linkTo(methodOn(PagoControllerV2.class).listarPagos()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pago por ID V2")
    public EntityModel<PagoDTO> obtenerPago(@PathVariable Long id) {
        log.info("GET /pagos/v2/{} - Obteniendo pago V2", id);

        PagoDTO pago = pagoService.obtenerPorId(id);

        return EntityModel.of(
                pago,
                linkTo(methodOn(PagoControllerV2.class).obtenerPago(id)).withSelfRel(),
                linkTo(methodOn(PagoControllerV2.class).listarPagos()).withRel("todos-los-pagos-v2"),
                linkTo(methodOn(PagoControllerV2.class).obtenerPagosPorOrden(pago.getOrdenId())).withRel("pagos-por-orden-v2")
        );
    }

    @GetMapping("/orden/{ordenId}")
    @Operation(summary = "Obtener pagos por orden V2")
    public CollectionModel<EntityModel<PagoDTO>> obtenerPagosPorOrden(@PathVariable Long ordenId) {
        log.info("GET /pagos/v2/orden/{} - Obteniendo pagos por orden V2", ordenId);

        List<EntityModel<PagoDTO>> pagos = pagoService.obtenerPorOrdenId(ordenId)
                .stream()
                .map(pago -> EntityModel.of(
                        pago,
                        linkTo(methodOn(PagoControllerV2.class).obtenerPago(pago.getId())).withSelfRel(),
                        linkTo(methodOn(PagoControllerV2.class).listarPagos()).withRel("todos-los-pagos-v2")
                ))
                .toList();

        return CollectionModel.of(
                pagos,
                linkTo(methodOn(PagoControllerV2.class).obtenerPagosPorOrden(ordenId)).withSelfRel(),
                linkTo(methodOn(PagoControllerV2.class).listarPagos()).withRel("todos-los-pagos-v2")
        );
    }
}