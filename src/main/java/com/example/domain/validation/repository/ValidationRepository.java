package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;

/**
 * Repository for Validation Aggregates.
 */
public interface ValidationRepository {
    void save(ValidationAggregate aggregate);
    ValidationAggregate find(String validationId);
}
