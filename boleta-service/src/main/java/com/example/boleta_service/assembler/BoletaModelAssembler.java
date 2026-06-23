package com.example.boleta_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.boleta_service.controller.BoletaController;
import com.example.boleta_service.model.Boleta;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class BoletaModelAssembler implements RepresentationModelAssembler<Boleta, EntityModel<Boleta>> {

    @Override
    public EntityModel<Boleta> toModel(Boleta boleta) {

        return EntityModel.of(boleta,

                linkTo(methodOn(BoletaController.class)
                        .buscarBoletaPorId(boleta.getId()))
                        .withSelfRel(),

                linkTo(methodOn(BoletaController.class)
                        .listarBoletas())
                        .withRel("boletas"));
    }
}