package com.example.domain.userinterfacenavigation.repository;

import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;

import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregates.
 */
public interface ScreenMapRepository {
    void save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String screenId);
}
