package com.example.envio_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Se desactiva porque requiere levantar el contexto completo con base de datos")
@SpringBootTest
class EnvioServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}