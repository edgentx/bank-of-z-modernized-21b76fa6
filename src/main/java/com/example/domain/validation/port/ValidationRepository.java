package com.example.domain.validation.port;

import com.example.domain.validation.model.ValidationAggregate;

import java.util.Optional;

public interface ValidationRepository {
    void save(ValidationAggregate aggregate);
    Optional<ValidationAggregate> findById(String id);
}
