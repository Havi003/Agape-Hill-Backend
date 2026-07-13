package com.agapehill.agape_hill_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MpesaRegisterUrlRequest {

    @JsonProperty("ShortCode")
    private String shortCode;

    @JsonProperty("ResponseType")
    private String responseType;

    @JsonProperty("ConfirmationURL")
    private String confirmationUrl;

    @JsonProperty("ValidationURL")
    private String validationUrl;
}