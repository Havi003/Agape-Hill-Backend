package com.agapehill.agape_hill_backend.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class AcademicTermRequest {
    private UUID academicYearId;
    private String termName;
    private LocalDate startDate;
    private LocalDate endDate;
}
