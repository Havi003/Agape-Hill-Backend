package com.agapehill.agape_hill_backend.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.agapehill.agape_hill_backend.domain.entity.MpesaTransactionEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MpesaTransactionRepository extends ReactiveCrudRepository<MpesaTransactionEntity, UUID> {

    Mono<MpesaTransactionEntity> findByTransId(String transId);

    Mono<Boolean> existsByTransId(String transId);

    Flux<MpesaTransactionEntity> findByStatus(String status);

    Flux<MpesaTransactionEntity> findByAdmissionNumberIgnoreCase(String admissionNumber);

    Flux<MpesaTransactionEntity> findByStudentIdOrderByReceivedAtDesc(UUID studentId);
}
