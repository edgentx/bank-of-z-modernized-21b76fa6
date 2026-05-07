package com.example.domain.validation.repository;

import com.example.domain.validation.ValidationAggregate;
import java.util.Optional;

/**
 * Repository interface for ValidationAggregate.
 * Follows the repository pattern defined in the domain layer.
 */
public interface ValidationRepository {
    void save(ValidationAggregate aggregate);
    Optional<ValidationAggregate> findById(String id);
}
