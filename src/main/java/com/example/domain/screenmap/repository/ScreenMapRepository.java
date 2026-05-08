package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;

import java.util.Optional;

/**
 * Repository interface for ScreenMapAggregate.
 * Placed in the repository package as per standard DDD layout.
 */
public interface ScreenMapRepository {
    void save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
