package com.example.soporte_service.controller;

import com.example.soporte_service.dto.SoporteDTO;
import com.example.soporte_service.service.SoporteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/soportes")
public class SoporteController {

    private final SoporteService soporteService;

    public SoporteController(SoporteService soporteService) {
        this.soporteService = soporteService;
    }

    // GET /soportes
    @GetMapping
    public ResponseEntity<List<SoporteDTO>> getAll() {
        return ResponseEntity.ok(soporteService.findAll());
    }

    // GET /soportes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SoporteDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(soporteService.findById(id));
    }

    // POST /soportes
    @PostMapping
    public ResponseEntity<SoporteDTO> create(@RequestBody SoporteDTO soporteDTO) {
        SoporteDTO nuevoSoporte = soporteService.save(soporteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoSoporte);
    }

    // PUT /soportes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<SoporteDTO> update(@PathVariable Long id, @RequestBody SoporteDTO soporteDTO) {
        return ResponseEntity.ok(soporteService.update(id, soporteDTO));
    }

    // DELETE /soportes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        soporteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET /soportes/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SoporteDTO>> getByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(soporteService.findByUsuarioId(usuarioId));
    }

    // GET /soportes/orden/{ordenId}
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<SoporteDTO>> getByOrdenId(@PathVariable Long ordenId) {
        return ResponseEntity.ok(soporteService.findByOrdenId(ordenId));
    }
}