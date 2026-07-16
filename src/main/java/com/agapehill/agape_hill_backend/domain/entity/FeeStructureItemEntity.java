package com.agapehill.agape_hill_backend.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "fee_structure_items", schema = "school")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureItemEntity {

    @Id
    private UUID id;

    @Column("fee_structure_id")
    private UUID feeStructureId;

    @Column("item_name")
    private String itemName;

    private BigDecimal amount;

    @Column("item_type")
    private String itemType;

    @Column("applies_to_class_group")
    private String appliesToClassGroup;

    private String description;

    @Column("is_active")
    private boolean active;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
