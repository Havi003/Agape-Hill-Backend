package com.agapehill.agape_hill_backend.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.agapehill.agape_hill_backend.domain.entity.NextOfKinEntity;

import reactor.core.publisher.Mono;

public interface NextOfKinRepository extends ReactiveCrudRepository <NextOfKinEntity, UUID> {

        Mono<NextOfKinEntity> findByStudentId(UUID studentId);
}
