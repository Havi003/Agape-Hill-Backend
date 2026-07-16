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

@Table(name = "fee_payments", schema = "school")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeePaymentEntity {

    @Id
    private UUID id;

    @Column("student_id")
    private UUID studentId;

    @Column("mpesa_transaction_id")
    private UUID mpesaTransactionId;

    @Column("academic_year_id")
    private UUID academicYearId;

    @Column("term_id")
    private UUID termId;

    private BigDecimal amount;

    @Column("payment_method")
    private String paymentMethod;

    private String reference;

    private String notes;

    @Column("paid_at")
    private LocalDateTime paidAt;

    @Column("recorded_at")
    private LocalDateTime recordedAt;

    public FeePaymentEntity(UUID id, UUID studentId, UUID mpesaTransactionId, BigDecimal amount, LocalDateTime paidAt, LocalDateTime recordedAt) {
        this.id = id;
        this.studentId = studentId;
        this.mpesaTransactionId = mpesaTransactionId;
        this.amount = amount;
        this.paymentMethod = "MPESA";
        this.paidAt = paidAt;
        this.recordedAt = recordedAt;
    }
}
