package com.example.producto_service.assembler;

import com.example.producto_service.controller.CategoriaController;
import com.example.producto_service.controller.GpuController;
import com.example.producto_service.dto.GpuDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class GpuModelAssembler implements RepresentationModelAssembler<GpuDTO, EntityModel<GpuDTO>> {

    @Override
    public EntityModel<GpuDTO> toModel(GpuDTO gpuDTO) {
        return EntityModel.of(
                gpuDTO,
                linkTo(methodOn(GpuController.class).buscarGpuPorId(gpuDTO.getId())).withSelfRel(),
                linkTo(methodOn(GpuController.class).listarGpus()).withRel("gpus"),
                linkTo(methodOn(GpuController.class).existeGpu(gpuDTO.getId())).withRel("exists"),
                linkTo(methodOn(CategoriaController.class).buscarCategoriaPorId(gpuDTO.getCategoriaId())).withRel("categoria"),
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withRel("categorias")
        );
    }
}