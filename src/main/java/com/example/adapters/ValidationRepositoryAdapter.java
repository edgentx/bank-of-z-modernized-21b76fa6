package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-Memory implementation of the Validation Repository.
 * Useful for testing and prototyping before persisting to DB2/Mongo.
 */
@Repository
public class ValidationRepositoryAdapter implements ValidationRepository {

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
