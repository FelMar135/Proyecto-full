package com.example.boleta_service.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.boleta_service.model.Boleta;
import com.example.boleta_service.repository.BoletaRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final BoletaRepository boletaRepository;

    public DataLoader(BoletaRepository boletaRepository) {
        this.boletaRepository = boletaRepository;
    }

    @Override
    public void run(String... args) {

        if (boletaRepository.count() == 0) {

            boletaRepository.save(new Boleta(
                    null,
                    1L,
                    1L,
                    100000.0,
                    19000.0,
                    119000.0,
                    LocalDate.now(),
                    "BOL-1001"
            ));

            boletaRepository.save(new Boleta(
                    null,
                    2L,
                    1L,
                    250000.0,
                    47500.0,
                    297500.0,
                    LocalDate.now(),
                    "BOL-1002"
            ));

            boletaRepository.save(new Boleta(
                    null,
                    3L,
                    2L,
                    300000.0,
                    57000.0,
                    357000.0,
                    LocalDate.now(),
                    "BOL-1003"
            ));

            System.out.println("Boletas de prueba cargadas correctamente");
        }
    }
}