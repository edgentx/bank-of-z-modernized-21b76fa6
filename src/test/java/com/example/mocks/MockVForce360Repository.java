package com.example.mocks;

import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.ports.VForce360Repository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Mock implementation of VForce360Repository for testing.
 */
@Component
public class MockVForce360Repository implements VForce360Repository {

    @Override
    public void save(VForce360Aggregate aggregate) {
        System.out.println("[MockRepo] Saving aggregate: " + aggregate.id());
    }

    @Override
    public Optional<VForce360Aggregate> findById(String id) {
        return Optional.empty();
    }
}
