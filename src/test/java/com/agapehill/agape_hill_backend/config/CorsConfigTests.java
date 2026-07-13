package com.agapehill.agape_hill_backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "FRONTEND_URL=https://agape-hill.netlify.app,http://localhost:5173")
class CorsConfigTests {

    private final WebTestClient webTestClient;

    @Autowired
    CorsConfigTests(@Value("${local.server.port}") int port) {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void allowsConfiguredFrontendOrigin() {
        webTestClient
                .options()
                .uri("/api/students")
                .header(HttpHeaders.ORIGIN, "https://agape-hill.netlify.app")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        "https://agape-hill.netlify.app");
    }

    @Test
    void rejectsUnconfiguredFrontendOrigin() {
        webTestClient
                .options()
                .uri("/api/students")
                .header(HttpHeaders.ORIGIN, "https://untrusted.example")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }
}
