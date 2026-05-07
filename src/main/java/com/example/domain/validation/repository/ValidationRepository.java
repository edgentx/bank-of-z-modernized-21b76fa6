package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;
import java.util.Optional;

/**
 * Repository interface for the Validation Aggregate.
 * In a real CQRS setup, this would handle persistence.
 */
public interface ValidationRepository {
    Optional<ValidationAggregate> findById(String id);
    void save(ValidationAggregate aggregate);
}
