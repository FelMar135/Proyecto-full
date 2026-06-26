package com.example.user_service.assembler;

import com.example.user_service.controller.UsuarioController;
import com.example.user_service.dto.UsuarioDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<UsuarioDTO, EntityModel<UsuarioDTO>> {

    @Override
    public EntityModel<UsuarioDTO> toModel(UsuarioDTO dto) {
        return EntityModel.of(dto,
                // Enlace al recurso individual (Self)
                linkTo(methodOn(UsuarioController.class).buscarUsuarioPorId(dto.getId())).withSelfRel(),
                
                // Enlace a la colección completa
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios")
        );
    }
}