package com.example.producto_service.dto;

import com.example.producto_service.model.Categoria;
import com.example.producto_service.model.Gpu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GpuDTO {

    private Long id;
    private String nombre;
    private String marca;
    private String modelo;
    private Integer vram;
    private Long precio;
    private String estado;

    private Long categoriaId;
    private Long stock;
    public Gpu toModel() {

        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);

        return new Gpu(
            id,
            nombre,
            marca,
            modelo,
            vram,
            precio,
            estado,
            stock,
            categoria
        );
    }

    public static GpuDTO fromModel(Gpu g) {

        if (g == null) return null;

        return new GpuDTO(
                g.getId(),
                g.getNombre(),
                g.getMarca(),
                g.getModelo(),
                g.getVram(),
                g.getPrecio(),
                g.getEstado(),
                g.getCategoria() != null ? g.getCategoria().getId() : null,
                g.getStock()
        );
    }
}