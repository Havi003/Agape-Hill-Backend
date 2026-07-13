package com.agapehill.agape_hill_backend.repository;

import com.agapehill.agape_hill_backend.domain.entity.StudentEventPaymentEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface StudentEventPaymentRepository extends ReactiveCrudRepository<StudentEventPaymentEntity, UUID> {
    
    Flux<StudentEventPaymentEntity> findByEventId(UUID eventId);
    
    Mono<StudentEventPaymentEntity> findByEventIdAndStudentId(UUID eventId, UUID studentId);

    // Dynamic reactive aggregate projections
    @Query("SELECT COUNT(student_id) FROM school.student_event_payments WHERE event_id = :eventId")
    Mono<Long> countParticipantsByEventId(UUID eventId);

    @Query("SELECT COALESCE(SUM(amount_paid), 0) FROM school.student_event_payments WHERE event_id = :eventId")
    Mono<BigDecimal> sumAmountPaidByEventId(UUID eventId);
}