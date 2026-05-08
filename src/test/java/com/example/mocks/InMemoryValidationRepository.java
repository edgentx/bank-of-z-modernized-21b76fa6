package com.example.mocks;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Mock adapter for ValidationRepository.
 * Uses in-memory Map to simulate persistence without external DB.
 */
public class InMemoryValidationRepository implements ValidationRepository {

    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public ValidationRepository save(ValidationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return this;
    }

    @Override
    public Optional<ValidationAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public ValidationAggregate create() {
        String id = UUID.randomUUID().toString();
        ValidationAggregate aggregate = new ValidationAggregate(id);
        store.put(id, aggregate);
        return aggregate;
    }

    // Helper for test cleanup if needed
    public void clear() {
        store.clear();
    }
}
