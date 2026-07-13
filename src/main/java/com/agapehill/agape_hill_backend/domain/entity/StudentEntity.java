package com.agapehill.agape_hill_backend.domain.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(schema = "school", name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentEntity {

    @Id
    private UUID id; // Set to null when creating a new record

    private String admissionNumber;
    private String fullName;
    private String gender;
     private String nemisNumber;
    private LocalDate dateOfBirth;
    private String studentClass;
    private LocalDate registeredDate;
   
}