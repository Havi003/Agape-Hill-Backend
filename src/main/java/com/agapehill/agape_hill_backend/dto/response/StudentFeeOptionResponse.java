package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentFeeOptionResponse {
    private UUID id;
    private UUID studentId;
    private UUID academicYearId;
    private UUID termId;
    private String optionName;
    private boolean enabled;
    private BigDecimal amountOverride;
}
