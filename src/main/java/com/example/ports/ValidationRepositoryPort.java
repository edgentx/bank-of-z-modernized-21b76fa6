package com.example.ports;

import com.example.domain.validation.model.ValidationAggregate;

/**
 * Repository Port.
 */
public interface ValidationRepositoryPort {
    ValidationAggregate load(String id);
    void save(ValidationAggregate aggregate);
}
