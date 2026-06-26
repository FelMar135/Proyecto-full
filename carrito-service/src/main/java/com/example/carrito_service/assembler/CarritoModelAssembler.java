package com.example.carrito_service.assembler;

import com.example.carrito_service.controller.CarritoController;
import com.example.carrito_service.dto.CarritoDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CarritoModelAssembler implements RepresentationModelAssembler<CarritoDTO, EntityModel<CarritoDTO>> {

    @Override
    public EntityModel<CarritoDTO> toModel(CarritoDTO dto) {

        return EntityModel.of(
                dto,

                linkTo(methodOn(CarritoController.class)
                        .obtenerPorId(dto.getId()))
                        .withSelfRel(),

                linkTo(methodOn(CarritoController.class)
                        .obtenerTodos())
                        .withRel("todos-los-carritos"),

                linkTo(methodOn(CarritoController.class)
                        .obtenerPorUsuario(dto.getUsuarioId()))
                        .withRel("carritos-por-usuario"),

                linkTo(methodOn(CarritoController.class)
                        .existePorId(dto.getId()))
                        .withRel("existe"),

                linkTo(methodOn(CarritoController.class)
                        .totalProductosPorUsuario(dto.getUsuarioId()))
                        .withRel("total-productos-usuario")
        );
    }
}