package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentStatementResponse {
    private UUID studentId;
    private String admissionNumber;
    private String fullName;
    private String studentClass;
    private BigDecimal totalBilled;
    private BigDecimal totalPaid;
    private BigDecimal balance;
    private List<StudentChargeResponse> charges;
    private List<StudentPaymentResponse> payments;
}
