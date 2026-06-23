package com.example.producto_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.producto_service.dto.GpuDTO;
import com.example.producto_service.model.Gpu;
import com.example.producto_service.service.GpuService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gpus")
public class GpuController {

    private final GpuService gpuService;

    public GpuController(GpuService gpuService) {
        this.gpuService = gpuService;
    }

    @PostMapping
    public ResponseEntity<GpuDTO> crearGpu(@RequestBody GpuDTO gpuDTO) {
        Gpu nueva = gpuService.guardar(gpuDTO.toModel());
        return ResponseEntity.ok(GpuDTO.fromModel(nueva));
    }

    @GetMapping
    public ResponseEntity<List<GpuDTO>> listarGpus() {
        List<Gpu> gpus = gpuService.listar();

        List<GpuDTO> dtos = gpus.stream()
                .map(GpuDTO::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GpuDTO> buscarGpuPorId(@PathVariable Long id) {
        Gpu gpu = gpuService.buscarPorId(id);

        if (gpu == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(GpuDTO.fromModel(gpu));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeGpu(@PathVariable Long id) {
        return ResponseEntity.ok(gpuService.existePorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GpuDTO> actualizarGpu(@PathVariable Long id,
                                                @RequestBody GpuDTO gpuDTO) {
        Gpu actualizada = gpuService.actualizar(id, gpuDTO.toModel());

        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(GpuDTO.fromModel(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGpu(@PathVariable Long id) {
        boolean eliminado = gpuService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}