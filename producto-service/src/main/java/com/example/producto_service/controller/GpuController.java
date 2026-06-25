package com.example.producto_service.controller;

import com.example.producto_service.assembler.GpuModelAssembler;
import com.example.producto_service.dto.GpuDTO;
import com.example.producto_service.model.Gpu;
import com.example.producto_service.service.GpuService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/gpus")
public class GpuController {

    private final GpuService gpuService;
    private final GpuModelAssembler gpuModelAssembler;

    public GpuController(GpuService gpuService, GpuModelAssembler gpuModelAssembler) {
        this.gpuService = gpuService;
        this.gpuModelAssembler = gpuModelAssembler;
    }

    @PostMapping
    public ResponseEntity<EntityModel<GpuDTO>> crearGpu(@RequestBody GpuDTO gpuDTO) {
        Gpu nueva = gpuService.guardar(gpuDTO.toModel());
        GpuDTO respuesta = GpuDTO.fromModel(nueva);

        return ResponseEntity.ok(gpuModelAssembler.toModel(respuesta));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<GpuDTO>>> listarGpus() {
        List<EntityModel<GpuDTO>> gpus = gpuService.listar()
                .stream()
                .map(GpuDTO::fromModel)
                .map(gpuModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<GpuDTO>> respuesta = CollectionModel.of(
                gpus,
                linkTo(methodOn(GpuController.class).listarGpus()).withSelfRel()
        );

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<GpuDTO>> buscarGpuPorId(@PathVariable Long id) {
        Gpu gpu = gpuService.buscarPorId(id);
        GpuDTO respuesta = GpuDTO.fromModel(gpu);

        return ResponseEntity.ok(gpuModelAssembler.toModel(respuesta));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeGpu(@PathVariable Long id) {
        return ResponseEntity.ok(gpuService.existePorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<GpuDTO>> actualizarGpu(
            @PathVariable Long id,
            @RequestBody GpuDTO gpuDTO
    ) {
        Gpu actualizada = gpuService.actualizar(id, gpuDTO.toModel());
        GpuDTO respuesta = GpuDTO.fromModel(actualizada);

        return ResponseEntity.ok(gpuModelAssembler.toModel(respuesta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGpu(@PathVariable Long id) {
        gpuService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}