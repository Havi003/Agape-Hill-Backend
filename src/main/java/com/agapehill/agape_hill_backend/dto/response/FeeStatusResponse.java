package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeStatusResponse {
    private BigDecimal totalBilled;
    private BigDecimal totalPaid;
    private BigDecimal balance;
}