package com.example.ports;

import com.example.domain.validation.model.ValidationAggregate;

import java.util.Optional;

public interface ValidationRepositoryPort {
    void save(ValidationAggregate aggregate);
    Optional<ValidationAggregate> findById(String id);
}
