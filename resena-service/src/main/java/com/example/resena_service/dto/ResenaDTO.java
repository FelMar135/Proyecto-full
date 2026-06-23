package com.example.resena_service.dto;

import com.example.resena_service.model.Resena;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResenaDTO {

    private Long id;
    private Long usuarioId;
    private Long gpuId;
    private String comentario;
    private Integer calificacion;
    private LocalDate fecha;

    // Método para convertir de DTO a la Entidad (Model)
    public Resena toModel() {
        return new Resena(
                id,
                usuarioId,
                gpuId,
                comentario,
                calificacion,
                fecha
        );
    }

    // Método estático para mapear desde la Entidad (Model) al DTO
    public static ResenaDTO fromModel(Resena r) {
        
        if (r == null) return null;

        return new ResenaDTO(
                r.getId(),
                r.getUsuarioId(),
                r.getGpuId(),
                r.getComentario(),
                r.getCalificacion(),
                r.getFecha()
        );
    }
}