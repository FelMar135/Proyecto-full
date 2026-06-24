package com.example.resena_service.controller;

import com.example.resena_service.assembler.ResenaModelAssembler;
import com.example.resena_service.dto.ResenaDTO;
import com.example.resena_service.service.ResenaService;
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
@RequestMapping("/resenas")
public class ResenaController {

    private final ResenaService resenaService;
    private final ResenaModelAssembler assembler;

    public ResenaController(ResenaService resenaService, ResenaModelAssembler assembler) {
        this.resenaService = resenaService;
        this.assembler = assembler;
    }

    // 1. OBTENER TODAS
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ResenaDTO>>> obtenerTodas() {
        List<EntityModel<ResenaDTO>> resenas = resenaService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(resenas,
                linkTo(methodOn(ResenaController.class).obtenerTodas()).withSelfRel()));
    }

    // 2. OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ResenaDTO>> obtenerPorId(@PathVariable Long id) {
        ResenaDTO resena = resenaService.findById(id);
        return ResponseEntity.ok(assembler.toModel(resena));
    }

    // 3. OBTENER POR USUARIO ID
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<ResenaDTO>>> obtenerPorUsuarioId(@PathVariable Long usuarioId) {
        List<EntityModel<ResenaDTO>> resenas = resenaService.findByUsuarioId(usuarioId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(resenas,
                linkTo(methodOn(ResenaController.class).obtenerPorUsuarioId(usuarioId)).withSelfRel()));
    }

    // 4. OBTENER POR GPU ID
    @GetMapping("/gpu/{gpuId}")
    public ResponseEntity<CollectionModel<EntityModel<ResenaDTO>>> obtenerPorGpuId(@PathVariable Long gpuId) {
        List<EntityModel<ResenaDTO>> resenas = resenaService.findByGpuId(gpuId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(resenas,
                linkTo(methodOn(ResenaController.class).obtenerPorGpuId(gpuId)).withSelfRel()));
    }

    // 5. VERIFICAR SI EXISTE POR ID
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.existsById(id));
    }

    // 6. CREAR (POST)
    @PostMapping
    public ResponseEntity<EntityModel<ResenaDTO>> crearResena(@RequestBody ResenaDTO dto) {
        ResenaDTO nuevaResena = resenaService.save(dto);
        EntityModel<ResenaDTO> entityModel = assembler.toModel(nuevaResena);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // 7. ACTUALIZAR (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ResenaDTO>> actualizarResena(@PathVariable Long id, @RequestBody ResenaDTO dto) {
        ResenaDTO resenaActualizada = resenaService.update(id, dto);
        return ResponseEntity.ok(assembler.toModel(resenaActualizada));
    }

    // 8. ELIMINAR (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarResena(@PathVariable Long id) {
        resenaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}