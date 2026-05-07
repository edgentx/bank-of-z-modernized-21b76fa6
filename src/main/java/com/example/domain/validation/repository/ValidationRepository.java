package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;

public interface ValidationRepository {
    void save(ValidationAggregate aggregate);
    ValidationAggregate load(String id);
}
