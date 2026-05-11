package com.example.mocks;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import java.util.HashMap;
import java.util.Map;

/** Mock adapter for ValidationRepository. */
public class InMemoryValidationRepository implements ValidationRepository {
    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public ValidationAggregate load(String defectId) {
        return store.getOrDefault(defectId, new ValidationAggregate(defectId));
    }

    @Override
    public void save(ValidationAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
