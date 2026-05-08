package com.example.mocks;

import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryVForce360Repository implements VForce360Repository {
    private final Map<String, VForce360Aggregate> store = new HashMap<>();

    @Override
    public VForce360Aggregate save(VForce360Aggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<VForce360Aggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}