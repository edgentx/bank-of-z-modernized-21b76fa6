package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.ValidationRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter for Validation Repository.
 * Currently a placeholder to satisfy dependency injection requirements.
 */
@Component
public class ValidationRepositoryAdapter implements ValidationRepositoryPort {

    @Override
    public ValidationAggregate save(ValidationAggregate aggregate) {
        // Placeholder implementation
        return aggregate;
    }

    @Override
    public Optional<ValidationAggregate> findById(String id) {
        // Placeholder implementation
        return Optional.empty();
    }
}