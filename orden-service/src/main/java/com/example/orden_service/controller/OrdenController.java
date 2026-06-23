package com.example.orden_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.orden_service.dto.OrdenDTO;
import com.example.orden_service.model.Orden;
import com.example.orden_service.service.OrdenService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping
public ResponseEntity<?> crearOrden(@RequestBody OrdenDTO ordenDTO) {

    try {
        Orden nueva = ordenService.guardar(ordenDTO.toModel());
        return ResponseEntity.ok(OrdenDTO.fromModel(nueva));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    @GetMapping
    public ResponseEntity<List<OrdenDTO>> listarOrdenes() {
        List<Orden> ordenes = ordenService.listar();

        List<OrdenDTO> dtos = ordenes.stream()
                .map(OrdenDTO::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenDTO> buscarOrdenPorId(@PathVariable Long id) {
        Orden orden = ordenService.buscarPorId(id);

        if (orden == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(OrdenDTO.fromModel(orden));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeOrden(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.existePorId(id));
    }

    @PutMapping("/{id}")
public ResponseEntity<?> actualizarOrden(@PathVariable Long id,
                                         @RequestBody OrdenDTO ordenDTO) {

    try {
        Orden actualizada = ordenService.actualizar(id, ordenDTO.toModel());

        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(OrdenDTO.fromModel(actualizada));

    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {
        boolean eliminada = ordenService.eliminar(id);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
public ResponseEntity<List<OrdenDTO>> buscarPorUsuario(@PathVariable Long usuarioId) {

    List<Orden> ordenes = ordenService.buscarPorUsuarioId(usuarioId);

    List<OrdenDTO> dtos = ordenes.stream()
            .map(OrdenDTO::fromModel)
            .collect(Collectors.toList());

    return ResponseEntity.ok(dtos);
}

    @GetMapping("/total-ventas")
    public ResponseEntity<Double> totalVentas() {
    return ResponseEntity.ok(ordenService.totalVentas());
}
}