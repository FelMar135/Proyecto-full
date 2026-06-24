package com.example.orden_service.assembler;

import com.example.orden_service.controller.OrdenController;
import com.example.orden_service.dto.OrdenDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class OrdenModelAssembler implements RepresentationModelAssembler<OrdenDTO, EntityModel<OrdenDTO>> {

    @Override
    public EntityModel<OrdenDTO> toModel(OrdenDTO ordenDTO) {
        return EntityModel.of(
                ordenDTO,
                linkTo(methodOn(OrdenController.class).buscarOrdenPorId(ordenDTO.getId())).withSelfRel(),
                linkTo(methodOn(OrdenController.class).listarOrdenes()).withRel("ordenes"),
                linkTo(methodOn(OrdenController.class).existeOrden(ordenDTO.getId())).withRel("exists"),
                linkTo(methodOn(OrdenController.class).buscarPorUsuario(ordenDTO.getUsuarioId())).withRel("ordenes-usuario"),
                linkTo(methodOn(OrdenController.class).totalVentas()).withRel("total-ventas")
        );
    }
}