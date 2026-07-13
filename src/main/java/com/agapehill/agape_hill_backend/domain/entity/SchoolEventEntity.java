package com.agapehill.agape_hill_backend.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

@Table(name = "school_events", schema = "school")
@Data
@NoArgsConstructor
public class SchoolEventEntity {
    @Id
    private UUID id;
    private String title;
    
    @Column("event_date")
    private LocalDate eventDate;
    
    @Column("event_time")
    private String eventTime;
    
    @Column("registration_fee")
    private BigDecimal registrationFee;
    
    @Column("created_at")
    private LocalDateTime createdAt;

    // Custom constructor matching your exact StudentEntity instantiation pattern
    public SchoolEventEntity(UUID id, String title, LocalDate eventDate, String eventTime, BigDecimal registrationFee, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.registrationFee = registrationFee;
        this.createdAt = createdAt;
    }
}