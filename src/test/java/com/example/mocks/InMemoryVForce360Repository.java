package com.example.mocks;

import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryVForce360Repository implements VForce360Repository {
    private final Map<String, VForce360Aggregate> store = new HashMap<>();

    @Override
    public VForce360Aggregate load(String id) {
        // Return existing or create new aggregate instance for the workflow
        return store.getOrDefault(id, new VForce360Aggregate(id));
    }

    @Override
    public void save(VForce360Aggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
