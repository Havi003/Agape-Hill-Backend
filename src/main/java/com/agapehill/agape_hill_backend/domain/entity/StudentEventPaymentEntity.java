package com.agapehill.agape_hill_backend.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

@Table(name = "student_event_payments", schema = "school")
@Data
@NoArgsConstructor
public class StudentEventPaymentEntity {
    @Id
    private UUID id;
    
    @Column("event_id")
    private UUID eventId;
    
    @Column("student_id")
    private UUID studentId;
    
    @Column("amount_paid")
    private BigDecimal amountPaid;
    
    @Column("logged_at")
    private LocalDateTime loggedAt;

    // Explicit constructor matching Option 2 pattern
    public StudentEventPaymentEntity(UUID id, UUID eventId, UUID studentId, BigDecimal amountPaid, LocalDateTime loggedAt) {
        this.id = id;
        this.eventId = eventId;
        this.studentId = studentId;
        this.amountPaid = amountPaid;
        this.loggedAt = loggedAt;
    }
}