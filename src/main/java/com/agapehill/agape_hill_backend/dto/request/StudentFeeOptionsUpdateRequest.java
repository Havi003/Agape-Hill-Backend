package com.agapehill.agape_hill_backend.dto.request;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class StudentFeeOptionsUpdateRequest {
    private UUID academicYearId;
    private UUID termId;
    private List<StudentFeeOptionRequest> options;
}
