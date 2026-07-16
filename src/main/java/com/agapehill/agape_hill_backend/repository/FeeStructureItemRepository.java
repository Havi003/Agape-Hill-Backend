package com.agapehill.agape_hill_backend.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.agapehill.agape_hill_backend.domain.entity.FeeStructureItemEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FeeStructureItemRepository extends ReactiveCrudRepository<FeeStructureItemEntity, UUID> {

    Flux<FeeStructureItemEntity> findByFeeStructureId(UUID feeStructureId);

    Flux<FeeStructureItemEntity> findByFeeStructureIdAndActiveTrue(UUID feeStructureId);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM school.fee_structure_items WHERE fee_structure_id = :feeStructureId AND item_type = :itemType AND is_active = TRUE")
    Mono<BigDecimal> sumByFeeStructureIdAndItemType(UUID feeStructureId, String itemType);
}
