package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentPaymentResponse {
    private UUID id;
    private String paymentMethod;
    private String transactionId;
    private String reference;
    private BigDecimal amount;
    private LocalDateTime paidAt;
}
