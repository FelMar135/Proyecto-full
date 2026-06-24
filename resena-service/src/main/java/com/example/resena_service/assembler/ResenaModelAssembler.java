package com.example.resena_service.assembler;

import com.example.resena_service.controller.ResenaController;
import com.example.resena_service.dto.ResenaDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ResenaModelAssembler implements RepresentationModelAssembler<ResenaDTO, EntityModel<ResenaDTO>> {

    @Override
    public EntityModel<ResenaDTO> toModel(ResenaDTO dto) {
        return EntityModel.of(
                dto,
                
                linkTo(methodOn(ResenaController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(ResenaController.class).obtenerTodas()).withRel("todas-las-resenas"),
                linkTo(methodOn(ResenaController.class).obtenerPorUsuarioId(dto.getUsuarioId())).withRel("resenas-por-usuario"),
                linkTo(methodOn(ResenaController.class).obtenerPorGpuId(dto.getGpuId())).withRel("resenas-por-gpu"),
                linkTo(methodOn(ResenaController.class).existePorId(dto.getId())).withRel("existe")
        );
    }
}