package com.example.domain.userinterfacenavigation.repository;

import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
    // Note: load() method from error trace might have been a typo for find or a specific load method. 
    // Assuming standard CRUD for now, but can add load(String id) if projection logic requires it differently.
    default ScreenMapAggregate load(String id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Aggregate not found: " + id));
    }
}
