package com.agapehill.agape_hill_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "mpesa")
public class MpesaProperties {
    private String baseUrl;
    private String consumerKey;
    private String consumerSecret;
    private String shortCode;
    private String confirmationUrl;
    private String validationUrl;
    private String responseType;
}