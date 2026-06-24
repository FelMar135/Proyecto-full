package com.example.soporte_service.assembler;

import com.example.soporte_service.controller.SoporteController;
import com.example.soporte_service.dto.SoporteDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class SoporteModelAssembler implements RepresentationModelAssembler<SoporteDTO, EntityModel<SoporteDTO>> {

    @Override
    public EntityModel<SoporteDTO> toModel(SoporteDTO dto) {
        EntityModel<SoporteDTO> entidad = EntityModel.of(
                dto,
                // Link a este ticket específico
                linkTo(methodOn(SoporteController.class).obtenerPorId(dto.getId())).withSelfRel(),
                
                // Link al listado global de tickets
                linkTo(methodOn(SoporteController.class).obtenerTodos()).withRel("todos-los-tickets"),
                
                // Links dinámicos para buscar por dependencias (El de la orden va abajo por seguridad)
                linkTo(methodOn(SoporteController.class).obtenerPorUsuarioId(dto.getUsuarioId())).withRel("tickets-por-usuario"),
                
                // Link de utilidad para verificar existencia
                linkTo(methodOn(SoporteController.class).existePorId(dto.getId())).withRel("existe")
        );

        // Agregamos el link de la orden SOLO si el ticket realmente tiene una orden.
        // Si pasamos un null aquí, Spring HATEOAS lanzaría una excepción.
        if (dto.getOrdenId() != null) {
            entidad.add(linkTo(methodOn(SoporteController.class).obtenerPorOrdenId(dto.getOrdenId())).withRel("tickets-por-orden"));
        }

        return entidad;
    }
}