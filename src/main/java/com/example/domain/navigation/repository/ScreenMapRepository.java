package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMapAggregate;
import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregates.
 * Following the hexagonal architecture pattern.
 */
public interface ScreenMapRepository {
    Optional<ScreenMapAggregate> findById(String id);
    void save(ScreenMapAggregate aggregate);
}