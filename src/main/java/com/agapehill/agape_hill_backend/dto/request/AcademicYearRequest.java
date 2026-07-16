package com.agapehill.agape_hill_backend.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AcademicYearRequest {
    private String yearName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
