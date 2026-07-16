package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentChargeResponse {
    private UUID id;
    private UUID termId;
    private String description;
    private String chargeType;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
