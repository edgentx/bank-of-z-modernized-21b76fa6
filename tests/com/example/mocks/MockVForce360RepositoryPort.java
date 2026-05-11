package com.example.mocks;

import com.example.domain.defect.model.DefectAggregate;
import com.example.ports.VForce360RepositoryPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mock adapter for VForce360 Repository.
 * Used in tests to simulate database behavior without actual I/O.
 */
public class MockVForce360RepositoryPort implements VForce360RepositoryPort {

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