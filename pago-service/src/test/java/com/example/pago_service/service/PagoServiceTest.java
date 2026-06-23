package com.example.pago_service.service;

import com.example.pago_service.dto.PagoDTO;
import com.example.pago_service.exception.BusinessException;
import com.example.pago_service.exception.ResourceNotFoundException;
import com.example.pago_service.model.Pago;
import com.example.pago_service.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private WebClient ordenWebClient;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void obtenerTodos_deberiaRetornarListaDePagos() {
        Pago pago = Pago.builder()
                .id(1L)
                .ordenId(10L)
                .monto(new BigDecimal("100000"))
                .metodoPago("DEBITO")
                .estado("PAGADO")
                .fechaPago(LocalDateTime.now())
                .build();

        when(pagoRepository.findAll()).thenReturn(List.of(pago));

        List<PagoDTO> resultado = pagoService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(10L, resultado.get(0).getOrdenId());
        assertEquals("DEBITO", resultado.get(0).getMetodoPago());
        assertEquals("PAGADO", resultado.get(0).getEstado());

        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornarPago() {
        Pago pago = Pago.builder()
                .id(1L)
                .ordenId(10L)
                .monto(new BigDecimal("150000"))
                .metodoPago("CREDITO")
                .estado("PENDIENTE")
                .fechaPago(LocalDateTime.now())
                .build();

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        PagoDTO resultado = pagoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getOrdenId());
        assertEquals("CREDITO", resultado.getMetodoPago());

        verify(pagoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pagoService.obtenerPorId(99L));

        verify(pagoRepository, times(1)).findById(99L);
    }

    @Test
    void calcularIva_conMontoValido_deberiaRetornarIva() {
        Double resultado = pagoService.calcularIva(100000.0);

        assertEquals(19000.0, resultado);
    }

    @Test
    void calcularIva_conMontoInvalido_deberiaLanzarBusinessException() {
        assertThrows(BusinessException.class, () -> pagoService.calcularIva(0.0));
        assertThrows(BusinessException.class, () -> pagoService.calcularIva(null));
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminarPago() {
        when(pagoRepository.existsById(1L)).thenReturn(true);

        pagoService.eliminar(1L);

        verify(pagoRepository, times(1)).existsById(1L);
        verify(pagoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        when(pagoRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> pagoService.eliminar(99L));

        verify(pagoRepository, times(1)).existsById(99L);
        verify(pagoRepository, never()).deleteById(99L);
    }
}