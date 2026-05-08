package com.example.domain.validation.port;

import com.example.domain.validation.model.ValidationAggregate;

import java.util.Optional;

/**
 * Repository interface for Validation Aggregates.
 * Adapters must implement this to persist validation state.
 */
public interface ValidationRepository {
    void save(ValidationAggregate aggregate);
    Optional<ValidationAggregate> findById(String validationId);
}
