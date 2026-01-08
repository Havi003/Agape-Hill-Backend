package com.agapehill.agape_hill_backend.domain.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table (schema = "school", name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    private String admissionNumber;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String studentClass;
    private LocalDate registeredDate;


    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}
