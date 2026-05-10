package com.example.ports;

import com.example.domain.validation.ValidationAggregate;

/**
 * Repository interface for Validation Aggregates.
 * This is the Port that adapters must implement.
 */
public interface ValidationRepository {
    void save(ValidationAggregate aggregate);
    ValidationAggregate load(String defectId);
}
