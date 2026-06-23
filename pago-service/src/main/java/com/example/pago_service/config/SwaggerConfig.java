package com.example.pago_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI pagoServiceOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:9096")
                .description("Servidor local de pago-service");

        Info info = new Info()
                .title("Pago Service API")
                .version("1.0.0")
                .description("API REST para gestionar pagos asociados a órdenes en GpuStore")
                .contact(new Contact()
                        .name("Equipo GpuStore")
                        .email("soporte@gpustore.cl"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}