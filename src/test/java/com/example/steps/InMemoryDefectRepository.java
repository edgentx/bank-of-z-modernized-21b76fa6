package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.ports.VForce360RepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the VForce360 Repository for testing.
 */
public class InMemoryDefectRepository implements VForce360RepositoryPort {

    private final Map<String, DefectAggregate> store = new HashMap<>();

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