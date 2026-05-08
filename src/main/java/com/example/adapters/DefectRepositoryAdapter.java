package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Temporary In-Memory implementation of ValidationRepository to allow compilation.
 * Real implementation would persist to DB2/MongoDB as per architecture.
 */
@Component
public class DefectRepositoryAdapter implements ValidationRepository {

    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public ValidationAggregate save(ValidationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<ValidationAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}