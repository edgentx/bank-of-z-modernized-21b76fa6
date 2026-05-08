package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of ValidationRepository.
 * In a production environment, this would interact with MongoDB (VForce360 shared).
 * Configured as a Spring Bean for dependency injection.
 */
@Component
public class ValidationRepositoryImpl implements ValidationRepository {

    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public void save(ValidationAggregate aggregate) {
        // Note: In a real MongoDB adapter, we would save the aggregate state and events here.
        // For the defect fix, we rely on the aggregate's state being held in memory.
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<ValidationAggregate> findById(String id) {
        // Note: In a real MongoDB adapter, we would reconstruct the aggregate from events/snapshot.
        return Optional.ofNullable(store.get(id));
    }
}
