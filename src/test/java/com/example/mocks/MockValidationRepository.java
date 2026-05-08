package com.example.mocks;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MockValidationRepository implements ValidationRepository {
    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public Optional<ValidationAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(ValidationAggregate aggregate) {
        // In a real repository, we might update the existing entity. 
        // Here we just put the reference.
        store.put(aggregate.id(), aggregate);
    }
}
