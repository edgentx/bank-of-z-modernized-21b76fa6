package com.example.mocks;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory implementation of VForce360Repository for testing.
 */
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

    @Override
    public VForce360Aggregate create() {
        String id = UUID.randomUUID().toString();
        return new VForce360Aggregate(id);
    }
    
    // Helper for tests to clear state
    public void clear() {
        store.clear();
    }
}
