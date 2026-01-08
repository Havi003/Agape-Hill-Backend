package com.agapehill.agape_hill_backend.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.agapehill.agape_hill_backend.domain.entity.FeeStatusEntity;

public interface FeesStatusRepository extends ReactiveCrudRepository <FeeStatusEntity, UUID>{}
