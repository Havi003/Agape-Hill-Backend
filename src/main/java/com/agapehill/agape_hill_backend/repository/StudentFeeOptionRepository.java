package com.agapehill.agape_hill_backend.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.agapehill.agape_hill_backend.domain.entity.StudentFeeOptionEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StudentFeeOptionRepository extends ReactiveCrudRepository<StudentFeeOptionEntity, UUID> {

    Flux<StudentFeeOptionEntity> findByStudentIdAndAcademicYearIdAndTermId(UUID studentId, UUID academicYearId, UUID termId);

    Flux<StudentFeeOptionEntity> findByStudentIdAndTermIdAndEnabledTrue(UUID studentId, UUID termId);

    Mono<StudentFeeOptionEntity> findByStudentIdAndAcademicYearIdAndTermIdAndOptionName(UUID studentId, UUID academicYearId, UUID termId, String optionName);
}
