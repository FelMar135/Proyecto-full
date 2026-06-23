package com.example.auth_service.dto;

import com.example.auth_service.model.AuthUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String rol;

    public AuthUser toModel() {
        return new AuthUser(
                id,
                username,
                email,
                password,
                rol
        );
    }

    public static AuthUserDTO fromModel(AuthUser a) {
        if (a == null) return null;

        return new AuthUserDTO(
                a.getId(),
                a.getUsername(),
                a.getEmail(),
                a.getPassword(),
                a.getRol()
        );
    }
}