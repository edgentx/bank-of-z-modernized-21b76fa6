package com.example.domain.vforce360.repository;

import com.example.domain.vforce360.model.VForce360Aggregate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repository for VForce360 Aggregate.
 * Note: In a real application this would likely persist to MongoDB or DB2.
 * For this defect fix, we focus on the logic within the Aggregate.
 */
public class VForce360Repository {
    private final Map<String, VForce360Aggregate> store = new HashMap<>();

    public void save(VForce360Aggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public Optional<VForce360Aggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
