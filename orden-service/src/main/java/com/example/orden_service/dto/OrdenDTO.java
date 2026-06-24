package com.example.orden_service.dto;

import com.example.orden_service.model.Orden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID del carrito es obligatorio")
    private Long carritoId;

    @NotNull(message = "El total de la orden es obligatorio")
    @Positive(message = "El total debe ser mayor a 0")
    private Double total;

    @NotBlank(message = "El estado de la orden es obligatorio")
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

    public static OrdenDTO fromModel(Orden orden) {
        if (orden == null) {
            return null;
        }

        return OrdenDTO.builder()
                .id(orden.getId())
                .usuarioId(orden.getUsuarioId())
                .carritoId(orden.getCarritoId())
                .total(orden.getTotal())
                .estado(orden.getEstado())
                .build();
    }
}