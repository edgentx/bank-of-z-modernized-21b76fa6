package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;

import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregate.
 */
public interface ScreenMapRepository {
    Optional<ScreenMapAggregate> findById(String id);
    void save(ScreenMapAggregate aggregate);
}
