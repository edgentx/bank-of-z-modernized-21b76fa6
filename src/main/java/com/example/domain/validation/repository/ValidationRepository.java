package com.example.domain.validation.repository;

import com.example.domain.validation.model.ValidationAggregate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for Validation aggregate.
 * In a real production environment, this would be implemented by JPA/Hibernate.
 */
public class ValidationRepository {
    private final Map<String, ValidationAggregate> store = new ConcurrentHashMap<>();

    public ValidationAggregate save(ValidationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    public ValidationAggregate findById(String id) {
        return store.get(id);
    }
}
