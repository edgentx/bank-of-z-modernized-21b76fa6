package com.example.domain.userinterfacenavigation.repository;

import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregates.
 * Previously referenced ScreenMapAggregate from wrong package; corrected to userinterfacenavigation.model.
 */
public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
