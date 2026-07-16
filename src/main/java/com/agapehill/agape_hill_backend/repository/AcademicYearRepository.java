package com.agapehill.agape_hill_backend.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.agapehill.agape_hill_backend.domain.entity.AcademicYearEntity;

import reactor.core.publisher.Mono;

@Repository
public interface AcademicYearRepository extends ReactiveCrudRepository<AcademicYearEntity, UUID> {

    Mono<AcademicYearEntity> findByYearName(String yearName);

    Mono<AcademicYearEntity> findByActiveTrue();
}
