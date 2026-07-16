package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeeStructureItemResponse {
    private UUID id;
    private String itemName;
    private BigDecimal amount;
    private String itemType;
    private String appliesToClassGroup;
    private String description;
    private boolean active;
}
