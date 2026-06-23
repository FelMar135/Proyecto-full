package com.example.orden_service.dto;

import com.example.orden_service.model.Orden;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenDTO {

    private Long id;
    private Long usuarioId;
    private Long carritoId;
    private Double total;
    private String estado;

    public Orden toModel() {
        return new Orden(
                id,
                usuarioId,
                carritoId,
                total,
                estado
        );
    }

    public static OrdenDTO fromModel(Orden o) {

        if (o == null) return null;

        return new OrdenDTO(
                o.getId(),
                o.getUsuarioId(),
                o.getCarritoId(),
                o.getTotal(),
                o.getEstado()
        );
    }
}