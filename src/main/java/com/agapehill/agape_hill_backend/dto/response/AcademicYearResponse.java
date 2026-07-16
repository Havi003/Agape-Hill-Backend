package com.agapehill.agape_hill_backend.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AcademicYearResponse {
    private UUID id;
    private String yearName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
