package com.agapehill.agape_hill_backend.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "academic_terms", schema = "school")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicTermEntity {

    @Id
    private UUID id;

    @Column("academic_year_id")
    private UUID academicYearId;

    @Column("term_name")
    private String termName;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    private String status;

    @Column("is_active")
    private boolean active;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
