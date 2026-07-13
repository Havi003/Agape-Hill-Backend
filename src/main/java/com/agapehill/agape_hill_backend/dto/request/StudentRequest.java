package com.agapehill.agape_hill_backend.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data

public class StudentRequest {
    
    private String fullName;
    
    private String studentGender;
    private LocalDate dateOfBirth;
    private String studentClass;

    private BigDecimal totalBilled;
    private BigDecimal totalPaid;

    private String kinName;
    private String kinRelationship;
    private String kinContact;
    private String kinAddress;
    private String kinEmail;
    private String nemisNumber;

}
