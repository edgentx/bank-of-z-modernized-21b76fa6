package com.example.domain.uinavigation.model;

/**
 * Repository interface for ScreenMap aggregate.
 */
public interface ScreenMapRepository {
    ScreenMapAggregate load(String id);
}
