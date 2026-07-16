package com.agapehill.agape_hill_backend.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class ManualPaymentRequest {
    private UUID studentId;
    private UUID academicYearId;
    private UUID termId;
    private BigDecimal amount;
    private String paymentMethod;
    private String reference;
    private String notes;
    private LocalDateTime paidAt;
}
