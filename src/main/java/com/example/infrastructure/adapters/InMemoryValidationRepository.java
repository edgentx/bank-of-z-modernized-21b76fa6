package com.example.infrastructure.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.port.ValidationRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the ValidationRepository.
 * Used for testing and rapid prototyping. Stores aggregates in a concurrent map.
 */
@Component
public class InMemoryValidationRepository implements ValidationRepository {

    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public void save(ValidationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<ValidationAggregate> findById(String validationId) {
        return Optional.ofNullable(store.get(validationId));
    }
}
