package com.agapehill.agape_hill_backend.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class StudentFeeOptionRequest {
    private String optionName;
    private boolean enabled;
    private BigDecimal amountOverride;
}
