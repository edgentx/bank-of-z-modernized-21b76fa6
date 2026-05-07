package com.example.domain.uimodel.repository;

import com.example.domain.uimodel.model.ScreenMapAggregate;

import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregates.
 */
public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);

    Optional<ScreenMapAggregate> findById(String screenId);
}
