package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateBillsResponse {
    private int generatedCount;
    private int skippedCount;
    private BigDecimal totalBilled;
    private String message;
}
