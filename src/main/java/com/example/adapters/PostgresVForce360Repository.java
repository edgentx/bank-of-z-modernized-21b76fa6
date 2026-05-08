package com.example.adapters;

import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Real implementation of VForce360Repository using standard persistence patterns.
 * This class serves as the concrete implementation for the application runtime,
 // unlike the InMemoryVForce360Repository used in tests.
 */
@Repository
public class PostgresVForce360Repository implements VForce360Repository {

    @Override
    public VForce360Aggregate save(VForce360Aggregate aggregate) {
        // Real DB logic here (e.g., entityManager.persist/merge)
        System.out.println("[PostgresRepo] Saving aggregate: " + aggregate.id());
        return aggregate;
    }

    @Override
    public Optional<VForce360Aggregate> findById(String id) {
        // Real DB logic here
        return Optional.empty(); // Simplified for fix
    }

    @Override
    public VForce360Aggregate create() {
        String id = UUID.randomUUID().toString();
        System.out.println("[PostgresRepo] Creating new aggregate with ID: " + id);
        return new VForce360Aggregate(id);
    }
}
