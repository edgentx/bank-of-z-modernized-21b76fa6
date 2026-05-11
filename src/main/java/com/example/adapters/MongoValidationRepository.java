package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import org.springframework.stereotype.Component;

/**
 * Mongo implementation of ValidationRepository.
 * S-FB-1: Needed to satisfy compiler.
 */
@Component
public class MongoValidationRepository implements ValidationRepository {

    @Override
    public ValidationAggregate load(String id) {
        return new ValidationAggregate(id);
    }

    @Override
    public void save(ValidationAggregate aggregate) {
        // Persist logic
    }
}
