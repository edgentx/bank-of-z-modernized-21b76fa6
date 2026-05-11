package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;

public interface ValidationRepository {
    ValidationAggregate load(String id);
    void save(ValidationAggregate aggregate);
}
