package com.example.mocks;

import com.example.domain.validation.ValidationAggregate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryValidationRepository {
    private final Map<String, ValidationAggregate> store = new HashMap<>();

    public void save(ValidationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public Optional<ValidationAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
