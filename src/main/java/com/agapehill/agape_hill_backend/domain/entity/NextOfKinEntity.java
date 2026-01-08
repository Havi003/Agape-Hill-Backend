package com.agapehill.agape_hill_backend.domain.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(schema="school", name= "next_of_kin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NextOfKinEntity implements Persistable<UUID> {

    @Id
    private UUID studentId;

    private String kinName;
    private String kinRelationship;
    private String kinContact;
    private String kinAdress;

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
