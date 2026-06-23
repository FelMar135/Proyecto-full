package com.example.pago_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoDTO {

    private Long id;

    @NotNull(message = "El ID de la orden es obligatorio")
    private Long ordenId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotBlank(message = "El método de pago es obligatorio")
    @Pattern(
            regexp = "DEBITO|CREDITO|TRANSFERENCIA",
            message = "El método de pago debe ser DEBITO, CREDITO o TRANSFERENCIA"
    )
    private String metodoPago;

    @NotBlank(message = "El estado del pago es obligatorio")
    @Pattern(
            regexp = "PENDIENTE|PAGADO|RECHAZADO",
            message = "El estado debe ser PENDIENTE, PAGADO o RECHAZADO"
    )
    private String estado;

    private LocalDateTime fechaPago;
}