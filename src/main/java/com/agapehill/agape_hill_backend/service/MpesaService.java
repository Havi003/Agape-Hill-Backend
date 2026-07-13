package com.agapehill.agape_hill_backend.service;

import com.agapehill.agape_hill_backend.dto.request.MpesaConfirmationRequest;
import com.agapehill.agape_hill_backend.dto.request.MpesaValidationRequest;
import com.agapehill.agape_hill_backend.dto.response.MpesaCallbackResponse;
import com.agapehill.agape_hill_backend.dto.response.MpesaRegisterUrlResponse;

import reactor.core.publisher.Mono;

public interface MpesaService {

    Mono<String> generateAccessToken();

    Mono<MpesaRegisterUrlResponse> registerC2BUrls();

    Mono<MpesaCallbackResponse> validatePayment(MpesaValidationRequest request);

    Mono<MpesaCallbackResponse> processConfirmation(MpesaConfirmationRequest request);
}