package com.example.envio_service.assembler;

import com.example.envio_service.controller.EnvioController;
import com.example.envio_service.dto.EnvioDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class EnvioModelAssembler implements RepresentationModelAssembler<EnvioDTO, EntityModel<EnvioDTO>> {

    @Override
    public EntityModel<EnvioDTO> toModel(EnvioDTO envioDTO) {
        return EntityModel.of(
                envioDTO,
                linkTo(methodOn(EnvioController.class).obtenerPorId(envioDTO.getId())).withSelfRel(),
                linkTo(methodOn(EnvioController.class).obtenerTodos()).withRel("todos-los-envios"),
                linkTo(methodOn(EnvioController.class).obtenerPorOrdenId(envioDTO.getOrdenId())).withRel("envios-por-orden"),
                linkTo(methodOn(EnvioController.class).existePorId(envioDTO.getId())).withRel("existe")
        );
    }
}