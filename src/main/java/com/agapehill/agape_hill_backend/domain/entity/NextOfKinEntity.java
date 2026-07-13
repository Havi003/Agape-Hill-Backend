package com.agapehill.agape_hill_backend.domain.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(schema = "school", name = "next_of_kin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NextOfKinEntity {

    @Id
    private UUID id; // Set to null when creating a new record (Primary Key)

    @Column("student_id")
    private UUID studentId; // Populated from savedStudent.getId() (Foreign Key)

    @NotBlank
    private String kinName;

    @Column("kin_relationship")
    private String kinRelationship;

    @Column("kin_contact")
    private String kinContact;

    @Column("kin_address")
    private String kinAddress;

    @Column("kin_email")
    private String kinEmail;
}