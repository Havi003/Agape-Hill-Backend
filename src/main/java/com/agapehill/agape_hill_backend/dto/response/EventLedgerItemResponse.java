package com.agapehill.agape_hill_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EventLedgerItemResponse {
    private UUID studentId;
    private String fullName;
    private String admissionNumber;
    private BigDecimal amountPaid;
}