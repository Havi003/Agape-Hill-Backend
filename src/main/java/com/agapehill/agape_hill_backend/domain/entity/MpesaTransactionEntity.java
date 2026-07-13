package com.agapehill.agape_hill_backend.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "mpesa_transactions", schema = "school")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpesaTransactionEntity {

    @Id
    private UUID id;

    @Column("trans_id")
    private String transId;

    @Column("trans_amount")
    private BigDecimal transAmount;

    @Column("bill_ref_number")
    private String billRefNumber;

    @Column("admission_number")
    private String admissionNumber;

    @Column("payment_purpose")
    private String paymentPurpose;

    private String msisdn;

    @Column("first_name")
    private String firstName;

    @Column("middle_name")
    private String middleName;

    @Column("last_name")
    private String lastName;

    @Column("student_id")
    private UUID studentId;

    private String status;

    @Column("raw_payload")
    private Json rawPayload;

    @Column("received_at")
    private LocalDateTime receivedAt;

    @Column("applied_at")
    private LocalDateTime appliedAt;

    @Column("result_message")
    private String resultMessage;
}
