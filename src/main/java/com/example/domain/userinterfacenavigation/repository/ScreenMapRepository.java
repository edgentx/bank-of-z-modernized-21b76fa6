package com.example.domain.userinterfacenavigation.repository;

import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregates.
 * Located in userinterfacenavigation to resolve previous build errors regarding misplaced aggregate definitions.
 */
public interface ScreenMapRepository {
    Optional<ScreenMapAggregate> findById(String id);
    void save(ScreenMapAggregate aggregate);
}
