package com.agapehill.agape_hill_backend.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table (schema="school" , name="fee_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeStatusEntity implements Persistable<UUID> {

    @Id
    private UUID studentId;

    private BigDecimal totalBilled;
    private BigDecimal totalPaid;
    private BigDecimal balance;

    @Transient
    private boolean isNew;

    @Override
    public UUID getId() {
        return this.studentId;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}
