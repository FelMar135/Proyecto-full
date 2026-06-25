package com.example.auth_service.assembler;

import com.example.auth_service.controller.AuthUserController;
import com.example.auth_service.model.AuthUser;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class AuthUserModelAssembler implements RepresentationModelAssembler<AuthUser, EntityModel<AuthUser>> {

    @Override
    public EntityModel<AuthUser> toModel(AuthUser authUser) {
        return EntityModel.of(
                authUser,
                linkTo(methodOn(AuthUserController.class).buscarUsuarioPorId(authUser.getId())).withSelfRel(),
                linkTo(methodOn(AuthUserController.class).listarUsuarios()).withRel("usuarios"),
                linkTo(methodOn(AuthUserController.class).existeUsuario(authUser.getId())).withRel("exists"),
                linkTo(methodOn(AuthUserController.class).buscarUsuarioPorEmail(authUser.getEmail())).withRel("email"),
                linkTo(methodOn(AuthUserController.class).obtenerRolPorEmail(authUser.getEmail())).withRel("rol")
        );
    }
}