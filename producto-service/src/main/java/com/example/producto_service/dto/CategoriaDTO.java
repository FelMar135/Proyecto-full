package com.example.producto_service.dto;

import com.example.producto_service.model.Categoria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CategoriaDTO {

    private Long id;
    private String nombre;
    private String descripcion;

    public Categoria toModel() {

        return new Categoria(
                id,
                nombre,
                descripcion,
                null
        );
    }

    public static CategoriaDTO fromModel(Categoria c) {

        if (c == null) return null;

        return new CategoriaDTO(
                c.getId(),
                c.getNombre(),
                c.getDescripcion()
        );
    }
}