package com.example.mocks;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository for ValidationAggregate testing.
 */
public class InMemoryValidationRepository implements ValidationRepository {

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
