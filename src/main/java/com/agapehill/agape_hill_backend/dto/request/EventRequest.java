package com.agapehill.agape_hill_backend.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class EventRequest {
    private String title;
    private LocalDate eventDate;
    private String eventTime;
    private BigDecimal registrationFee;
    
    // Field for logging incoming transactions
    private UUID studentId;
    private BigDecimal amountPaid;
}