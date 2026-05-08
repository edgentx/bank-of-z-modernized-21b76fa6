package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the ValidationRepository.
 * This adapter persists the aggregate state in memory.
 * In a production environment, this would be implemented with JPA/MongoDB.
 */
@Repository
public class ValidationRepositoryAdapter implements ValidationRepository {
    
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