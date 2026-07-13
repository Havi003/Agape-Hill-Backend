package com.agapehill.agape_hill_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EventSummaryResponse {
    private UUID id;
    private String title;
    private LocalDate eventDate;
    private String eventTime;
    private BigDecimal registrationFee;
    private long participantCount;
    private BigDecimal totalCollected;
}