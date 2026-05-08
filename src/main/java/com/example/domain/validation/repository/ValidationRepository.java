package com.example.domain.validation.repository;

import com.example.domain.validation.ValidationAggregate;
import java.util.Optional;

/**
 * Repository interface for Validation Aggregates.
 * Implementations would handle persistence (e.g., MongoDB, JPA).
 */
public interface ValidationRepository {
    Optional<ValidationAggregate> findById(String defectId);
    void save(ValidationAggregate aggregate);
}
