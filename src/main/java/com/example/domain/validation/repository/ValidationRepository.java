package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;

import java.util.Optional;

public interface ValidationRepository {
    ValidationAggregate save(ValidationAggregate aggregate);
    Optional<ValidationAggregate> findById(String id);
}