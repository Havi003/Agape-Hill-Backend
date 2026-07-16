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

@Table(name = "student_fee_options", schema = "school")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeOptionEntity {

    @Id
    private UUID id;

    @Column("student_id")
    private UUID studentId;

    @Column("academic_year_id")
    private UUID academicYearId;

    @Column("term_id")
    private UUID termId;

    @Column("option_name")
    private String optionName;

    private boolean enabled;

    @Column("amount_override")
    private BigDecimal amountOverride;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
