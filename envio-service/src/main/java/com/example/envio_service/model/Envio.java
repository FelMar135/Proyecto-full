package com.example.envio_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(name = "direccion_envio", nullable = false, length = 150)
    private String direccionEnvio;

    @Column(name = "comuna", nullable = false, length = 80)
    private String comuna;

    @Column(name = "ciudad", nullable = false, length = 80)
    private String ciudad;

    @Column(name = "empresa_transportista", nullable = false, length = 80)
    private String empresaTransportista;

    @Column(name = "numero_seguimiento", length = 80)
    private String numeroSeguimiento;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;
}