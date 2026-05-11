package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMapAggregate;

import java.util.Optional;

/**
 * Repository interface for ScreenMap Aggregate.
 */
public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
