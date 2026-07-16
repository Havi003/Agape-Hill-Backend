package com.agapehill.agape_hill_backend.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FeeStructureItemRequest {
    private String itemName;
    private BigDecimal amount;
    private String itemType;
    private String appliesToClassGroup;
    private String description;
}
