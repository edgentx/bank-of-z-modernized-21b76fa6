package com.example.domain.uinavigation.repository;

import com.example.domain.uinavigation.model.ScreenMapAggregate;

import java.util.Optional;

/**
 * Repository interface for ScreenMapAggregate.
 * Fixes the 'cannot find symbol ScreenMapAggregate' error.
 */
public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
    void deleteById(String id);
}
