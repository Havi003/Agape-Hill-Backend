package com.agapehill.agape_hill_backend.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.agapehill.agape_hill_backend.domain.entity.StudentChargeEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StudentChargeRepository extends ReactiveCrudRepository<StudentChargeEntity, UUID> {

    Flux<StudentChargeEntity> findByStudentIdOrderByCreatedAtDesc(UUID studentId);

    Flux<StudentChargeEntity> findByStudentIdAndTermIdOrderByCreatedAtDesc(UUID studentId, UUID termId);

    Mono<Boolean> existsByStudentIdAndTermId(UUID studentId, UUID termId);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM school.student_charges WHERE student_id = :studentId AND status = 'ACTIVE'")
    Mono<BigDecimal> sumActiveChargesByStudentId(UUID studentId);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM school.student_charges WHERE student_id = :studentId AND term_id = :termId AND status = 'ACTIVE'")
    Mono<BigDecimal> sumActiveChargesByStudentIdAndTermId(UUID studentId, UUID termId);
}
