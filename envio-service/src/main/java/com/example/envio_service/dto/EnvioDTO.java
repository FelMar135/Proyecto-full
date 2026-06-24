package com.example.envio_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioDTO {

    private Long id;

    @NotNull(message = "El ID de la orden es obligatorio")
    private Long ordenId;

    @NotBlank(message = "La dirección de envío es obligatoria")
    @Size(max = 150, message = "La dirección no puede superar los 150 caracteres")
    private String direccionEnvio;

    @NotBlank(message = "La comuna es obligatoria")
    @Size(max = 80, message = "La comuna no puede superar los 80 caracteres")
    private String comuna;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 80, message = "La ciudad no puede superar los 80 caracteres")
    private String ciudad;

    @NotBlank(message = "La empresa transportista es obligatoria")
    @Size(max = 80, message = "La empresa transportista no puede superar los 80 caracteres")
    private String empresaTransportista;

    @Size(max = 80, message = "El número de seguimiento no puede superar los 80 caracteres")
    private String numeroSeguimiento;

    @NotBlank(message = "El estado del envío es obligatorio")
    @Pattern(
            regexp = "EN_PREPARACION|EN_TRANSITO|ENTREGADO|CANCELADO",
            message = "El estado debe ser EN_PREPARACION, EN_TRANSITO, ENTREGADO o CANCELADO"
    )
    private String estado;

    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaEntregaEstimada;
}