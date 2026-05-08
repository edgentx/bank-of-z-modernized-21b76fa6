package com.example.domain.validation.repository;

import com.example.domain.validation.model.Validation;
import java.util.Optional;

public interface ValidationRepository {
    Validation save(Validation validation);
    Optional<Validation> findById(String id);
}
