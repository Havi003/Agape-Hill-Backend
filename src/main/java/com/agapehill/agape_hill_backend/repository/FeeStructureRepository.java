package com.agapehill.agape_hill_backend.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.agapehill.agape_hill_backend.domain.entity.FeeStructureEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FeeStructureRepository extends ReactiveCrudRepository<FeeStructureEntity, UUID> {

    Flux<FeeStructureEntity> findByAcademicYearIdAndTermIdAndClassGroup(UUID academicYearId, UUID termId, String classGroup);

    Flux<FeeStructureEntity> findByAcademicYearIdAndTermId(UUID academicYearId, UUID termId);

    Mono<FeeStructureEntity> findByTermIdAndClassGroupAndStatus(UUID termId, String classGroup, String status);
}
