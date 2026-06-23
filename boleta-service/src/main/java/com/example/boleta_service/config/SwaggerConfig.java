package com.example.boleta_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Boleta Service API")
                        .version("1.0")
                        .description("Microservicio encargado de gestionar boletas")
                        .contact(new Contact()
                                .name("Grupo Proyecto Full")
                                .email("equipo@proyecto.cl")));
    }
}