package com.agapehill.agape_hill_backend.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.agapehill.agape_hill_backend.domain.entity.FeePaymentEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FeePaymentRepository extends ReactiveCrudRepository<FeePaymentEntity, UUID> {

    Flux<FeePaymentEntity> findByStudentIdOrderByPaidAtDesc(UUID studentId);

    Mono<FeePaymentEntity> findByMpesaTransactionId(UUID mpesaTransactionId);

    Mono<Boolean> existsByMpesaTransactionId(UUID mpesaTransactionId);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM school.fee_payments WHERE student_id = :studentId")
    Mono<BigDecimal> sumAmountByStudentId(UUID studentId);
}
