package com.example.domain.validation.port;

import com.example.domain.validation.model.ValidationAggregate;
import java.util.Optional;

/**
 * Repository interface for the Validation Aggregate.
 * Part of the domain layer; infrastructure adapters must implement this.
 */
public interface ValidationRepository {
    void save(ValidationAggregate aggregate);
    Optional<ValidationAggregate> findById(String id);
}
