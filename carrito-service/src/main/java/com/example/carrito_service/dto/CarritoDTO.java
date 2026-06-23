package com.example.carrito_service.dto;

import com.example.carrito_service.model.Carrito;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoDTO {

    private Long id;
    private Long usuarioId;
    private Long gpuId;
    private Integer cantidad;

    public Carrito toModel() {
        return new Carrito(
                id,
                usuarioId,
                gpuId,
                cantidad
        );
    }

    public static CarritoDTO fromModel(Carrito c) {

        if (c == null) return null;

        return new CarritoDTO(
                c.getId(),
                c.getUsuarioId(),
                c.getGpuId(),
                c.getCantidad()
        );
    }
}