package com.example.envio_service.config;

import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataLoaderTest {

    @Test
    void run_cuandoNoHayEnvios_deberiaInsertarDatos() {
        EnvioRepository envioRepository = mock(EnvioRepository.class);
        when(envioRepository.count()).thenReturn(0L);

        DataLoader dataLoader = new DataLoader(envioRepository);

        dataLoader.run();

        ArgumentCaptor<List<Envio>> captor = ArgumentCaptor.forClass(List.class);

        verify(envioRepository, times(1)).saveAll(captor.capture());

        List<Envio> envios = captor.getValue();

        assertFalse(envios.isEmpty());
        assertEquals(1L, envios.get(0).getOrdenId());
        assertNotNull(envios.get(0).getDireccionEnvio());
        assertNotNull(envios.get(0).getEstado());
    }

    @Test
    void run_cuandoYaHayEnvios_noDebeInsertarDatos() {
        EnvioRepository envioRepository = mock(EnvioRepository.class);
        when(envioRepository.count()).thenReturn(10L);

        DataLoader dataLoader = new DataLoader(envioRepository);

        dataLoader.run();

        verify(envioRepository, never()).saveAll(any());
    }
}