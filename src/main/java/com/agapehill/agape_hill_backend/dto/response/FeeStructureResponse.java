package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeeStructureResponse {
    private UUID id;
    private UUID academicYearId;
    private UUID termId;
    private String classGroup;
    private String name;
    private String status;
    private BigDecimal totalCompulsory;
    private BigDecimal totalOptional;
    private List<FeeStructureItemResponse> items;
}
