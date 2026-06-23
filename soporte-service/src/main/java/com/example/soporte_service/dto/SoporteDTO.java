package com.example.soporte_service.dto;

import com.example.soporte_service.model.Soporte;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoporteDTO {

    private Long id;
    private Long usuarioId; 
    private Long ordenId;   
    private String asunto;
    private String descripcion;
    private String estado; 
    private LocalDate fechaCreacion;

    public Soporte toModel() {
        return new Soporte(
                id,
                usuarioId,
                ordenId,
                asunto,
                descripcion,
                estado,
                fechaCreacion
        );
    }

    public static SoporteDTO fromModel(Soporte s) {

        if (s == null) return null;

        return new SoporteDTO(
            s.getId(),
            s.getUsuarioId(),
            s.getOrdenId(),
            s.getAsunto(),
            s.getDescripcion(),
            s.getEstado(),
            s.getFechaCreacion()
        );
    }
}