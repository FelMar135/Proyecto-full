package com.example.envio_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI envioServiceOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:9099")
                .description("Servidor local de envio-service");

        Info info = new Info()
                .title("Envio Service API")
                .version("1.0.0")
                .description("API REST para gestionar envíos asociados a órdenes en GpuStore")
                .contact(new Contact()
                        .name("Equipo GpuStore")
                        .email("soporte@gpustore.cl"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}