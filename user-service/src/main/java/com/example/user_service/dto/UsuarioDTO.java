package com.example.user_service.dto;

import com.example.user_service.model.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;

    public Usuario toModel() {
        return new Usuario(
                id,
                nombre,
                apellido,
                email,
                telefono,
                direccion
        );
    }

    public static UsuarioDTO fromModel(Usuario u) {
        if (u == null) return null;

        return new UsuarioDTO(
                u.getId(),
                u.getNombre(),
                u.getApellido(),
                u.getEmail(),
                u.getTelefono(),
                u.getDireccion()
        );
    }
}