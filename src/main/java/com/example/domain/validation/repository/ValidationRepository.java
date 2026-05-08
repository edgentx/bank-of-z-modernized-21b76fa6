package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;

import java.util.Optional;

/**
 * Repository interface for ValidationAggregate.
 */
public interface ValidationRepository {
    Optional<ValidationAggregate> findById(String id);
    void save(ValidationAggregate aggregate);
}
