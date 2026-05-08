package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.ValidationRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the ValidationRepositoryPort.
 * NOTE: In a real production environment, this would interact with the persistence layer.
 */
@Component
public class ValidationRepositoryAdapter implements ValidationRepositoryPort {

    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public void save(ValidationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<ValidationAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
