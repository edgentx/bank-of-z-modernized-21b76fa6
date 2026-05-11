package com.example.mocks;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.ports.VForce360RepositoryPort;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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