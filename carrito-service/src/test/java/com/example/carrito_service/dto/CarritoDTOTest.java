package com.example.carrito_service.dto;

import com.example.carrito_service.model.Carrito;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarritoDTOTest {

    @Test
    void debeConvertirDTOAModel() {

        CarritoDTO dto = new CarritoDTO(
                1L,
                10L,
                20L,
                3
        );

        Carrito carrito = dto.toModel();

        assertNotNull(carrito);
        assertEquals(1L, carrito.getId());
        assertEquals(10L, carrito.getUsuarioId());
        assertEquals(20L, carrito.getGpuId());
        assertEquals(3, carrito.getCantidad());
    }

    @Test
    void debeConvertirModelADTO() {

        Carrito carrito = new Carrito(
                1L,
                10L,
                20L,
                3
        );

        CarritoDTO dto = CarritoDTO.fromModel(carrito);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getUsuarioId());
        assertEquals(20L, dto.getGpuId());
        assertEquals(3, dto.getCantidad());
    }

    @Test
    void fromModelDebeRetornarNullSiModelEsNull() {

        CarritoDTO dto = CarritoDTO.fromModel(null);

        assertNull(dto);
    }
}