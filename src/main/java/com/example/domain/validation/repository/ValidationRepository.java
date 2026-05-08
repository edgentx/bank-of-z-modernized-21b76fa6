package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;
import java.util.Optional;

/**
 * Repository interface for ValidationAggregate.
 * Temporal workflows require durable state, but the aggregate itself
 * handles the business logic execution.
 */
public interface ValidationRepository {
    ValidationAggregate save(ValidationAggregate aggregate);
    Optional<ValidationAggregate> findById(String id);
}