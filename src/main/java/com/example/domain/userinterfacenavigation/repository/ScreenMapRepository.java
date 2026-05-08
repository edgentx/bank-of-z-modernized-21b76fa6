package com.example.domain.userinterfacenavigation.repository;

import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;

import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregates.
 * This fixes the compilation errors by referencing the correctly located Aggregate.
 */
public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
    ScreenMapAggregate load(String id); // Convenience method often used in tests/command handlers
}
