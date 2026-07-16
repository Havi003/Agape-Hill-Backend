package com.agapehill.agape_hill_backend.dto.request;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class GenerateBillsRequest {
    private UUID academicYearId;
    private UUID termId;
    private String classGroup;
    private List<UUID> studentIds;
}
