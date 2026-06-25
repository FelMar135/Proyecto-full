package com.example.pago_service.config;

import com.example.pago_service.model.Pago;
import com.example.pago_service.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataLoaderTest {

    @Test
    void run_cuandoNoHayPagos_deberiaInsertarDatos() {
        PagoRepository pagoRepository = mock(PagoRepository.class);
        when(pagoRepository.count()).thenReturn(0L);

        DataLoader dataLoader = new DataLoader(pagoRepository);

        dataLoader.run();

        ArgumentCaptor<List<Pago>> captor = ArgumentCaptor.forClass(List.class);

        verify(pagoRepository, times(1)).saveAll(captor.capture());

        List<Pago> pagos = captor.getValue();

        assertFalse(pagos.isEmpty());
        assertEquals(1L, pagos.get(0).getOrdenId());
        assertNotNull(pagos.get(0).getMonto());
        assertNotNull(pagos.get(0).getMetodoPago());
        assertNotNull(pagos.get(0).getEstado());
    }

    @Test
    void run_cuandoYaHayPagos_noDebeInsertarDatos() {
        PagoRepository pagoRepository = mock(PagoRepository.class);
        when(pagoRepository.count()).thenReturn(10L);

        DataLoader dataLoader = new DataLoader(pagoRepository);

        dataLoader.run();

        verify(pagoRepository, never()).saveAll(any());
    }
}