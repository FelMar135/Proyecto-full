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

        if (envioRepository.count() == 0) {
            List<Envio> envios = List.of(
                    Envio.builder()
                            .ordenId(1L)
                            .direccionEnvio("Av. Providencia 1234")
                            .comuna("Providencia")
                            .ciudad("Santiago")
                            .empresaTransportista("Chilexpress")
                            .numeroSeguimiento("ENV-1001")
                            .estado("EN_PREPARACION")
                            .fechaEnvio(LocalDateTime.now())
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(3))
                            .build(),

                    Envio.builder()
                            .ordenId(2L)
                            .direccionEnvio("Los Leones 550")
                            .comuna("Ñuñoa")
                            .ciudad("Santiago")
                            .empresaTransportista("Starken")
                            .numeroSeguimiento("ENV-1002")
                            .estado("EN_TRANSITO")
                            .fechaEnvio(LocalDateTime.now())
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(2))
                            .build(),

                    Envio.builder()
                            .ordenId(3L)
                            .direccionEnvio("Camino El Alba 890")
                            .comuna("Las Condes")
                            .ciudad("Santiago")
                            .empresaTransportista("Blue Express")
                            .numeroSeguimiento("ENV-1003")
                            .estado("ENTREGADO")
                            .fechaEnvio(LocalDateTime.now().minusDays(3))
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(1))
                            .build()
            );

            envioRepository.saveAll(envios);

            log.info("DataLoader insertó envíos iniciales correctamente");
        } else {
            log.info("DataLoader no insertó datos porque ya existen envíos");
        }
    }
}