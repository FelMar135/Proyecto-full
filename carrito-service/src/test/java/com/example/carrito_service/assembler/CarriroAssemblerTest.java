package com.example.carrito_service.assembler;

import com.example.carrito_service.dto.CarritoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import static org.junit.jupiter.api.Assertions.*;

class CarritoModelAssemblerTest {

    private final CarritoModelAssembler assembler = new CarritoModelAssembler();

    @Test
    void debeCrearModeloConLinksHateoas() {

        CarritoDTO dto = new CarritoDTO();
        dto.setId(1L);
        dto.setUsuarioId(10L);
        dto.setGpuId(20L);
        dto.setCantidad(2);

        EntityModel<CarritoDTO> model = assembler.toModel(dto);

        assertNotNull(model);
        assertEquals(dto, model.getContent());

        assertTrue(model.hasLink("self"));
        assertTrue(model.hasLink("todos-los-carritos"));
        assertTrue(model.hasLink("carritos-por-usuario"));
        assertTrue(model.hasLink("existe"));
        assertTrue(model.hasLink("total-productos-usuario"));
    }
}