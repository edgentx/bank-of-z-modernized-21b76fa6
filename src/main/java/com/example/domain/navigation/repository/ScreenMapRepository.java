package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMapAggregate;

import java.util.Optional;

/**
 * Repository interface for ScreenMapAggregate.
 * Located in src/main/java/com/example/domain/navigation/repository/
 */
public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
    // Exists check or custom queries if needed
}