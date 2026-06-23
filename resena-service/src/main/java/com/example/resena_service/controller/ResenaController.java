package com.example.resena_service.controller;

import com.example.resena_service.dto.ResenaDTO;
import com.example.resena_service.service.ResenaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping
    public ResponseEntity<List<ResenaDTO>> getAll() {
        return ResponseEntity.ok(resenaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.findById(id));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.existsById(id));
    }

    @PostMapping
    public ResponseEntity<ResenaDTO> create(@RequestBody ResenaDTO resenaDTO) {
        ResenaDTO nuevaResena = resenaService.save(resenaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaResena);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResenaDTO> update(@PathVariable Long id, @RequestBody ResenaDTO resenaDTO) {
        return ResponseEntity.ok(resenaService.update(id, resenaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resenaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaDTO>> getByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.findByUsuarioId(usuarioId));
    }

    @GetMapping("/gpu/{gpuId}")
    public ResponseEntity<List<ResenaDTO>> getByGpuId(@PathVariable Long gpuId) {
        return ResponseEntity.ok(resenaService.findByGpuId(gpuId));
    }
}