package com.agapehill.agape_hill_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MpesaWebClientConfig {

    private final MpesaProperties mpesaProperties;

    @Bean
    public WebClient mpesaWebClient() {
        return WebClient.builder()
                .baseUrl(mpesaProperties.getBaseUrl())
                .build();
    }
}