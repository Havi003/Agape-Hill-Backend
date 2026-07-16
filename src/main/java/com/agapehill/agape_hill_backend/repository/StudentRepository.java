package com.agapehill.agape_hill_backend.repository;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.agapehill.agape_hill_backend.domain.entity.StudentEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StudentRepository extends ReactiveCrudRepository <StudentEntity, UUID>{

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(admission_number FROM 3) AS INTEGER)), 0) " +
           "FROM school.students WHERE admission_number ~ '^AH[0-9]+$'")
    Mono<Integer> findHighestAgapeHillAdmissionSequence();

// Counts by the 'gender' field in your entity
    Mono<Long> countByGenderIgnoreCase(String gender);

    // Counts by the 'registeredDate' field in your entity
    Mono<Long> countByRegisteredDateAfter(LocalDate date);

    
    Mono<StudentEntity> findByAdmissionNumberIgnoreCase(String admissionNumber);

    // Supports searching by Name or Admission Number as shown in the search bar
    @Query("SELECT * FROM school.students WHERE " +
           "LOWER(full_name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(admission_number) LIKE LOWER(CONCAT('%', :search, '%'))")
    Flux<StudentEntity> searchStudents(String search);
    
    // To show "Total: X students registered" in the header
    Mono<Long> count();

}
