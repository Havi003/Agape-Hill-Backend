package com.agapehill.agape_hill_backend.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.agapehill.agape_hill_backend.domain.entity.NextOfKinEntity;

public interface NextOfKinRepository extends ReactiveCrudRepository <NextOfKinEntity, UUID> {}
