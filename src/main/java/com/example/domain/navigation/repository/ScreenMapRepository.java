package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMapAggregate;

/**
 * Repository interface for the ScreenMap Aggregate.
 */
public interface ScreenMapRepository {
    ScreenMapAggregate load(String id);
    void save(ScreenMapAggregate aggregate);
}
