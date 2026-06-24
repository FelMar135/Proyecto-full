package com.example.envio_service.controller;

import com.example.envio_service.dto.EnvioDTO;
import com.example.envio_service.service.EnvioService;
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
@RequestMapping("/envios/v2")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Envios V2", description = "Versión 2 de la API de envíos con HATEOAS")
public class EnvioControllerV2 {

    private final EnvioService envioService;

    @GetMapping
    @Operation(summary = "Listar todos los envíos V2")
    public CollectionModel<EntityModel<EnvioDTO>> listarEnvios() {
        log.info("GET /envios/v2 - Listando envíos V2");

        List<EntityModel<EnvioDTO>> envios = envioService.obtenerTodos()
                .stream()
                .map(envio -> EntityModel.of(
                        envio,
                        linkTo(methodOn(EnvioControllerV2.class).obtenerEnvio(envio.getId())).withSelfRel(),
                        linkTo(methodOn(EnvioControllerV2.class).listarEnvios()).withRel("todos-los-envios-v2")
                ))
                .toList();

        return CollectionModel.of(
                envios,
                linkTo(methodOn(EnvioControllerV2.class).listarEnvios()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un envío por ID V2")
    public EntityModel<EnvioDTO> obtenerEnvio(@PathVariable Long id) {
        log.info("GET /envios/v2/{} - Obteniendo envío V2", id);

        EnvioDTO envio = envioService.obtenerPorId(id);

        return EntityModel.of(
                envio,
                linkTo(methodOn(EnvioControllerV2.class).obtenerEnvio(id)).withSelfRel(),
                linkTo(methodOn(EnvioControllerV2.class).listarEnvios()).withRel("todos-los-envios-v2")
        );
    }
}