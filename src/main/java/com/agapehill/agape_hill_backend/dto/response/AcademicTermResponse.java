package com.agapehill.agape_hill_backend.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AcademicTermResponse {
    private UUID id;
    private UUID academicYearId;
    private String termName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private boolean active;
}
