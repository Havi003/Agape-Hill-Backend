package com.agapehill.agape_hill_backend.repository;

import com.agapehill.agape_hill_backend.domain.entity.SchoolEventEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SchoolEventRepository extends ReactiveCrudRepository<SchoolEventEntity, UUID> {
}