package com.example.mocks;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepositoryPort;

import java.util.Optional;

/**
 * Mock repository for testing.
 */
public class InMemoryDefectRepository implements DefectRepositoryPort {
    private final java.util.Map<String, DefectAggregate> store = new java.util.HashMap<>();

    @Override
    public DefectAggregate save(DefectAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<DefectAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
