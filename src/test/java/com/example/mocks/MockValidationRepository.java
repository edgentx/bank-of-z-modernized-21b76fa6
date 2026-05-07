package com.example.mocks;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock Adapter for Validation Repository.
 * Stores aggregates in memory for testing purposes.
 */
public class MockValidationRepository implements ValidationRepository {

    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public void save(ValidationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public ValidationAggregate load(String id) {
        return store.get(id);
    }
}
