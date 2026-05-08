package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;

public interface ValidationRepository {
    ValidationAggregate save(ValidationAggregate aggregate);
    ValidationAggregate findById(String id);
}
