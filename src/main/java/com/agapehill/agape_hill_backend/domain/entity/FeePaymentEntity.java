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

    private BigDecimal amount;

    @Column("paid_at")
    private LocalDateTime paidAt;

    @Column("recorded_at")
    private LocalDateTime recordedAt;
}
