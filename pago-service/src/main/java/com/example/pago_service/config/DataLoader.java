package com.example.pago_service.config;

import com.example.pago_service.model.Pago;
import com.example.pago_service.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private final PagoRepository pagoRepository;

    @Override
    public void run(String... args) {
        log.info("Ejecutando DataLoader de pago-service");

        if (pagoRepository.count() == 0) {
            List<Pago> pagos = List.of(
                    Pago.builder()
                            .ordenId(1L)
                            .monto(new BigDecimal("399990"))
                            .metodoPago("DEBITO")
                            .estado("PAGADO")
                            .fechaPago(LocalDateTime.now())
                            .build(),

                    Pago.builder()
                            .ordenId(2L)
                            .monto(new BigDecimal("899990"))
                            .metodoPago("TRANSFERENCIA")
                            .estado("PENDIENTE")
                            .fechaPago(LocalDateTime.now())
                            .build(),

                    Pago.builder()
                            .ordenId(3L)
                            .monto(new BigDecimal("599990"))
                            .metodoPago("CREDITO")
                            .estado("PAGADO")
                            .fechaPago(LocalDateTime.now())
                            .build()
            );

            pagoRepository.saveAll(pagos);

            log.info("DataLoader insertó pagos iniciales correctamente");
        } else {
            log.info("DataLoader no insertó datos porque ya existen pagos");
        }
    }
}