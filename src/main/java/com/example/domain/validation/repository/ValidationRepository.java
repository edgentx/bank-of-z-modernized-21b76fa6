package com.example.domain.validation.repository;

/**
 * Marker interface for Validation aggregates if persistence is needed.
 * Currently acts as a factory for Temporal Workflow stubs in tests.
 */
public interface ValidationRepository {
    // Future state: save(ValidationAggregate aggregate);
}
