package com.example.boleta_service.config;

import com.example.boleta_service.model.Boleta;
import com.example.boleta_service.repository.BoletaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final BoletaRepository boletaRepository;

    public DataLoader(BoletaRepository boletaRepository) {
        this.boletaRepository = boletaRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Ejecutando DataLoader de boleta-service ===");

        if (boletaRepository.count() == 0) {
            List<Boleta> boletas = List.of(
                    new Boleta(
                            null,
                            1L,
                            1L,
                            100000.0,
                            19000.0,
                            119000.0,
                            LocalDate.now(),
                            "BOL-1001"
                    ),
                    new Boleta(
                            null,
                            2L,
                            1L,
                            250000.0,
                            47500.0,
                            297500.0,
                            LocalDate.now(),
                            "BOL-1002"
                    ),
                    new Boleta(
                            null,
                            3L,
                            2L,
                            300000.0,
                            57000.0,
                            357000.0,
                            LocalDate.now(),
                            "BOL-1003"
                    ),
                    new Boleta(
                            null,
                            4L,
                            2L,
                            180000.0,
                            34200.0,
                            214200.0,
                            LocalDate.now(),
                            "BOL-1004"
                    ),
                    new Boleta(
                            null,
                            5L,
                            3L,
                            420000.0,
                            79800.0,
                            499800.0,
                            LocalDate.now(),
                            "BOL-1005"
                    ),
                    new Boleta(
                            null,
                            6L,
                            3L,
                            150000.0,
                            28500.0,
                            178500.0,
                            LocalDate.now(),
                            "BOL-1006"
                    ),
                    new Boleta(
                            null,
                            7L,
                            4L,
                            599990.0,
                            113998.1,
                            713988.1,
                            LocalDate.now(),
                            "BOL-1007"
                    ),
                    new Boleta(
                            null,
                            8L,
                            4L,
                            899990.0,
                            170998.1,
                            1070988.1,
                            LocalDate.now(),
                            "BOL-1008"
                    ),
                    new Boleta(
                            null,
                            9L,
                            5L,
                            399990.0,
                            75998.1,
                            475988.1,
                            LocalDate.now(),
                            "BOL-1009"
                    ),
                    new Boleta(
                            null,
                            10L,
                            5L,
                            1299990.0,
                            246998.1,
                            1546988.1,
                            LocalDate.now(),
                            "BOL-1010"
                    )
            );

            boletaRepository.saveAll(boletas);

            System.out.println("=== DataLoader insertó 10 boletas iniciales ===");
        } else {
            System.out.println("=== DataLoader no insertó porque ya existen boletas ===");
        }
    }
}