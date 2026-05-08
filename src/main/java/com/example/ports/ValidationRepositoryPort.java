package com.example.ports;

import com.example.domain.validation.model.ValidationAggregate;

import java.util.Optional;

/**
 * Port interface for Validation Repository.
 */
public interface ValidationRepositoryPort {
    ValidationAggregate save(ValidationAggregate aggregate);
    Optional<ValidationAggregate> findById(String id);
}