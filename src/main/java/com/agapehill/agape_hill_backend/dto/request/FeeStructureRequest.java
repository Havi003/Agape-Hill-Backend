package com.agapehill.agape_hill_backend.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class FeeStructureRequest {
    private UUID academicYearId;
    private UUID termId;
    private String classGroup;
    private String name;
}
