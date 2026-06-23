package com.example.carrito_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.carrito_service.dto.CarritoDTO;
import com.example.carrito_service.model.Carrito;
import com.example.carrito_service.service.CarritoService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping
    public ResponseEntity<?> crearCarrito(@RequestBody CarritoDTO carritoDTO) {
    try {
        Carrito nuevo = carritoService.guardar(carritoDTO.toModel());
        return ResponseEntity.ok(CarritoDTO.fromModel(nuevo));
    } catch (RuntimeException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
    }

    @GetMapping
    public ResponseEntity<List<CarritoDTO>> listarCarritos() {
        List<Carrito> carritos = carritoService.listar();

        List<CarritoDTO> dtos = carritos.stream()
                .map(CarritoDTO::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoDTO> buscarCarritoPorId(@PathVariable Long id) {
        Carrito carrito = carritoService.buscarPorId(id);

        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(CarritoDTO.fromModel(carrito));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeCarrito(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.existePorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarritoDTO> actualizarCarrito(@PathVariable Long id,
                                                        @RequestBody CarritoDTO carritoDTO) {
        Carrito actualizado = carritoService.actualizar(id, carritoDTO.toModel());

        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(CarritoDTO.fromModel(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCarrito(@PathVariable Long id) {
        boolean eliminado = carritoService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
public ResponseEntity<List<CarritoDTO>> buscarPorUsuario(@PathVariable Long usuarioId) {

    List<Carrito> carritos = carritoService.buscarPorUsuarioId(usuarioId);

    List<CarritoDTO> dtos = carritos.stream()
            .map(CarritoDTO::fromModel)
            .collect(Collectors.toList());

    return ResponseEntity.ok(dtos);
}

@GetMapping("/total-productos/{usuarioId}")
public ResponseEntity<Integer> totalProductosPorUsuario(@PathVariable Long usuarioId) {
    return ResponseEntity.ok(carritoService.totalProductosPorUsuario(usuarioId));
}
}