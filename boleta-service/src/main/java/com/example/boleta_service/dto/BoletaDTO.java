package com.example.boleta_service.dto;

import java.time.LocalDate;

import com.example.boleta_service.model.Boleta;

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
public class BoletaDTO {

    private Long id;

    @NotNull(message = "El ID de la orden es obligatorio")
    @Positive(message = "El ID de la orden debe ser mayor a 0")
    private Long ordenId;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser mayor a 0")
    private Long usuarioId;

    @NotNull(message = "El subtotal es obligatorio")
    @Positive(message = "El subtotal debe ser mayor a 0")
    private Double subtotal;

    private Double iva;

    private Double total;

    private LocalDate fechaEmision;

    private String numeroBoleta;

    public Boleta toModel() {
        return new Boleta(
                id,
                ordenId,
                usuarioId,
                subtotal,
                iva,
                total,
                fechaEmision,
                numeroBoleta
        );
    }

    public static BoletaDTO fromModel(Boleta b) {
        if (b == null) return null;

        return new BoletaDTO(
                b.getId(),
                b.getOrdenId(),
                b.getUsuarioId(),
                b.getSubtotal(),
                b.getIva(),
                b.getTotal(),
                b.getFechaEmision(),
                b.getNumeroBoleta()
        );
    }
}