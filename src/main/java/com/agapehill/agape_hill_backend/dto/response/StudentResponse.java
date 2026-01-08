package com.agapehill.agape_hill_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class StudentResponse {

    private UUID id;
    private String admissionNumber;
    private String fullName;
    private String studentClass;
    private String studentGender;
    private LocalDate registeredDate;
    private LocalDate dateOfBirth;

    private FeeStatusResponse feeStatus;

    private String kinName;
    private String kinRelationship;
    private String kinContact;

}
