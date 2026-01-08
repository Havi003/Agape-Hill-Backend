package com.agapehill.agape_hill_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class StudentDashboardResponse {
    
    private long totalStudents;
    private long maleStudents;
    private long femaleStudents;
    private long registrationsThisMonth;
    private double malePercentage;
    private double femalePercentage;


}
