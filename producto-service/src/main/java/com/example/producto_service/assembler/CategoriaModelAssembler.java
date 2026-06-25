package com.example.producto_service.assembler;

import com.example.producto_service.controller.CategoriaController;
import com.example.producto_service.controller.GpuController;
import com.example.producto_service.dto.CategoriaDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CategoriaModelAssembler implements RepresentationModelAssembler<CategoriaDTO, EntityModel<CategoriaDTO>> {

    @Override
    public EntityModel<CategoriaDTO> toModel(CategoriaDTO categoriaDTO) {
        return EntityModel.of(
                categoriaDTO,
                linkTo(methodOn(CategoriaController.class).buscarCategoriaPorId(categoriaDTO.getId())).withSelfRel(),
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withRel("categorias"),
                linkTo(methodOn(CategoriaController.class).existeCategoria(categoriaDTO.getId())).withRel("exists"),
                linkTo(methodOn(GpuController.class).listarGpus()).withRel("gpus")
        );
    }
}