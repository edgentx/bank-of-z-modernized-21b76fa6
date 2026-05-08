package com.example.adapters;

import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PostgresVForce360Repository implements VForce360Repository {
    @Override
    public Optional<VForce360Aggregate> findById(String id) {
        // Mock implementation
        return Optional.empty();
    }

    @Override
    public void save(VForce360Aggregate aggregate) {
        // Mock implementation
    }
}
