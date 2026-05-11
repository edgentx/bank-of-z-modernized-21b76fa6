package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;

public interface ValidationRepository {
    ValidationAggregate load(String defectId);
    void save(ValidationAggregate aggregate);
}
