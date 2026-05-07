package com.example.domain.uimodel.repository;

import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import java.util.Optional;

/**
 * Legacy/Alternative package reference for ScreenMap Repository.
 * Bridges uimodel package to the concrete userinterfacenavigation aggregate.
 */
public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
