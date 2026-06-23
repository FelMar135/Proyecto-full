package com.example.envio_service.config;

import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private final EnvioRepository envioRepository;

    @Override
    public void run(String... args) {
        log.info("Ejecutando DataLoader de envio-service");

        if (envioRepository.count() < 5) {
            List<Envio> envios = List.of(
                    Envio.builder()
                            .ordenId(4L)
                            .direccionEnvio("Av. Vicuña Mackenna 1200")
                            .comuna("Santiago")
                            .ciudad("Santiago")
                            .empresaTransportista("Chilexpress")
                            .numeroSeguimiento("ENV-4004")
                            .estado("EN_PREPARACION")
                            .fechaEnvio(LocalDateTime.now())
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(3))
                            .build(),

                    Envio.builder()
                            .ordenId(5L)
                            .direccionEnvio("Av. Grecia 750")
                            .comuna("Ñuñoa")
                            .ciudad("Santiago")
                            .empresaTransportista("Starken")
                            .numeroSeguimiento("ENV-5005")
                            .estado("EN_TRANSITO")
                            .fechaEnvio(LocalDateTime.now())
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(2))
                            .build()
            );

            envioRepository.saveAll(envios);

            log.info("DataLoader insertó envíos iniciales correctamente");
        } else {
            log.info("DataLoader no insertó datos porque ya existen envíos suficientes");
        }
    }
}