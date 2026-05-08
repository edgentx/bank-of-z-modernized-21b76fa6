package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;

import java.util.Optional;

/**
 * Repository interface for the Validation Aggregate.
 * Should be implemented by infrastructure adapters (e.g., MongoDB).
 */
public interface ValidationRepository {
    void save(ValidationAggregate aggregate);
    Optional<ValidationAggregate> findById(String id);
}
