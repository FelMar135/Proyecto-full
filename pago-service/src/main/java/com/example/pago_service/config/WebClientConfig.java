package com.example.pago_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${orden.service.url}")
    private String ordenServiceUrl;

    @Bean
    public WebClient ordenWebClient() {
        return WebClient.builder()
                .baseUrl(ordenServiceUrl)
                .build();
    }
}