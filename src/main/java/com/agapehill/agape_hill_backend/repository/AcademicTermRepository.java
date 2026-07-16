package com.agapehill.agape_hill_backend.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.agapehill.agape_hill_backend.domain.entity.AcademicTermEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AcademicTermRepository extends ReactiveCrudRepository<AcademicTermEntity, UUID> {

    Flux<AcademicTermEntity> findByAcademicYearId(UUID academicYearId);

    Mono<AcademicTermEntity> findByActiveTrue();
}
