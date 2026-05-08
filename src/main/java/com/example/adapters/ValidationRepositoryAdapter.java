package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.ValidationRepositoryPort;
import org.springframework.stereotype.Repository;

/**
 * Repository Stub for Validation Aggregate.
 * Implementing interface to resolve compilation errors.
 */
@Repository
public class ValidationRepositoryAdapter implements ValidationRepositoryPort {
    @Override
    public ValidationAggregate load(String id) {
        return new ValidationAggregate(id);
    }
    @Override
    public void save(ValidationAggregate aggregate) {}
}
