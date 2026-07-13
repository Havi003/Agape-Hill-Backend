package com.agapehill.agape_hill_backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agapehill.agape_hill_backend.dto.request.MpesaConfirmationRequest;
import com.agapehill.agape_hill_backend.dto.request.MpesaValidationRequest;
import com.agapehill.agape_hill_backend.dto.response.MpesaCallbackResponse;
import com.agapehill.agape_hill_backend.service.MpesaService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mpesa")
@RequiredArgsConstructor
public class MpesaController {

    private final MpesaService mpesaService;

    @PostMapping("/validation")
    public Mono<MpesaCallbackResponse> validatePayment(@RequestBody MpesaValidationRequest request) {
        return mpesaService.validatePayment(request);
    }

    @PostMapping("/confirmation")
    public Mono<MpesaCallbackResponse> processConfirmation(@RequestBody MpesaConfirmationRequest request) {
        return mpesaService.processConfirmation(request);
    }
}
