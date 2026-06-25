package com.example.envio_service.config;

import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
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
                            .numeroSeguimiento("CHX-1001")
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
                            .numeroSeguimiento("STK-2002")
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
                            .numeroSeguimiento("BLU-3003")
                            .estado("ENTREGADO")
                            .fechaEnvio(LocalDateTime.now().minusDays(3))
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(1))
                            .build(),

                    Envio.builder()
                            .ordenId(4L)
                            .direccionEnvio("Av. Vicuña Mackenna 1200")
                            .comuna("Santiago")
                            .ciudad("Santiago")
                            .empresaTransportista("Chilexpress")
                            .numeroSeguimiento("CHX-4004")
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
                            .numeroSeguimiento("STK-5005")
                            .estado("EN_TRANSITO")
                            .fechaEnvio(LocalDateTime.now())
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(3))
                            .build(),

                    Envio.builder()
                            .ordenId(6L)
                            .direccionEnvio("San Diego 450")
                            .comuna("Santiago")
                            .ciudad("Santiago")
                            .empresaTransportista("Blue Express")
                            .numeroSeguimiento("BLU-6006")
                            .estado("ENTREGADO")
                            .fechaEnvio(LocalDateTime.now().minusDays(2))
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(1))
                            .build(),

                    Envio.builder()
                            .ordenId(7L)
                            .direccionEnvio("Av. Apoquindo 3000")
                            .comuna("Las Condes")
                            .ciudad("Santiago")
                            .empresaTransportista("Chilexpress")
                            .numeroSeguimiento("CHX-7007")
                            .estado("CANCELADO")
                            .fechaEnvio(LocalDateTime.now())
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(4))
                            .build(),

                    Envio.builder()
                            .ordenId(8L)
                            .direccionEnvio("Gran Avenida 8500")
                            .comuna("La Cisterna")
                            .ciudad("Santiago")
                            .empresaTransportista("Starken")
                            .numeroSeguimiento("STK-8008")
                            .estado("EN_PREPARACION")
                            .fechaEnvio(LocalDateTime.now())
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(3))
                            .build(),

                    Envio.builder()
                            .ordenId(9L)
                            .direccionEnvio("Av. Independencia 980")
                            .comuna("Independencia")
                            .ciudad("Santiago")
                            .empresaTransportista("Blue Express")
                            .numeroSeguimiento("BLU-9009")
                            .estado("EN_TRANSITO")
                            .fechaEnvio(LocalDateTime.now())
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(4))
                            .build(),

                    Envio.builder()
                            .ordenId(10L)
                            .direccionEnvio("Pedro de Valdivia 1500")
                            .comuna("Providencia")
                            .ciudad("Santiago")
                            .empresaTransportista("Chilexpress")
                            .numeroSeguimiento("CHX-1010")
                            .estado("ENTREGADO")
                            .fechaEnvio(LocalDateTime.now().minusDays(3))
                            .fechaEntregaEstimada(LocalDateTime.now().plusDays(1))
                            .build()
            );

            envioRepository.saveAll(envios);

            log.info("DataLoader insertó 10 envíos iniciales correctamente");
        } else {
            log.info("DataLoader no insertó datos porque ya existen envíos");
        }
    }
}