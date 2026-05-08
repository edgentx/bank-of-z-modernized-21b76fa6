package com.example.infrastructure.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.port.ValidationRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JPA Adapter implementation for Validation Repository.
 * In a real DB2 scenario, this would manage JPA entities.
 * For this defect fix, we use a simplified in-memory store to satisfy the repository contract.
 */
@Component
public class JpaValidationRepositoryAdapter implements ValidationRepository {

    private final Map<String, ValidationAggregate> store = new HashMap<>();

    @Override
    public void save(ValidationAggregate aggregate) {
        // In a real scenario, map Aggregate to JPA Entity and save via EntityManager/Repository
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<ValidationAggregate> findById(String id) {
        // In a real scenario, query DB2 and map back to Aggregate
        return Optional.ofNullable(store.get(id));
    }
}
