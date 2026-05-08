package com.example.domain.screen.repository;

import com.example.domain.screen.model.ScreenMapAggregate;

import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregates.
 */
public interface ScreenMapRepository {

    ScreenMapAggregate save(ScreenMapAggregate aggregate);

    Optional<ScreenMapAggregate> findById(String id);

    ScreenMapAggregate load(String id);
}