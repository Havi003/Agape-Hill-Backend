package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeeDashboardResponse {
    private UUID academicYearId;
    private UUID termId;
    private BigDecimal totalBilled;
    private BigDecimal totalPaid;
    private BigDecimal totalBalance;
    private BigDecimal totalCredit;
    private long activeChargeCount;
    private long paymentCount;
    private long studentCount;
    private long billedStudentCount;
    private long configuredFeeStructures;
    private long publishedFeeStructures;
    private long draftFeeStructures;
    private boolean configurationReady;
    private String readinessMessage;
}
