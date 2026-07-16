package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillingPreviewResponse {
    private UUID studentId;
    private String admissionNumber;
    private String fullName;
    private String studentClass;
    private BigDecimal compulsoryTotal;
    private BigDecimal optionalTotal;
    private BigDecimal previousBalance;
    private BigDecimal credit;
    private BigDecimal newTermBill;
    private BigDecimal finalBalance;
}
