package com.example.mocks;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryValidationRepository implements ValidationRepository {
    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public ValidationAggregate save(ValidationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public ValidationAggregate findById(String id) {
        return store.get(id);
    }
}
