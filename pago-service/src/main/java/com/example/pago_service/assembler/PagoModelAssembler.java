package com.example.pago_service.assembler;

import com.example.pago_service.controller.PagoController;
import com.example.pago_service.dto.PagoDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PagoModelAssembler implements RepresentationModelAssembler<PagoDTO, EntityModel<PagoDTO>> {

    @Override
    public EntityModel<PagoDTO> toModel(PagoDTO pagoDTO) {
        return EntityModel.of(
                pagoDTO,
                linkTo(methodOn(PagoController.class).obtenerPorId(pagoDTO.getId())).withSelfRel(),
                linkTo(methodOn(PagoController.class).obtenerTodos()).withRel("todos-los-pagos"),
                linkTo(methodOn(PagoController.class).obtenerPorOrdenId(pagoDTO.getOrdenId())).withRel("pagos-por-orden"),
                linkTo(methodOn(PagoController.class).existePorId(pagoDTO.getId())).withRel("existe")
        );
    }
}