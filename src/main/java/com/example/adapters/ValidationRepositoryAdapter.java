package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.ValidationRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ValidationRepositoryAdapter implements ValidationRepositoryPort {

    // In-memory store for this implementation to resolve compilation.
    // In a real scenario, this would interface with MongoDB or DB2.
    private final Map<String, ValidationAggregate> store = new ConcurrentHashMap<>();

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
