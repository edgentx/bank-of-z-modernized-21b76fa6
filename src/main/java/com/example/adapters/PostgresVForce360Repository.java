package com.example.adapters;

import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.ports.VForce360RepositoryPort;
import org.springframework.stereotype.Repository;

/**
 * Repository Stub for VForce360 Aggregate.
 * Implementing interface to resolve compilation errors.
 */
@Repository
public class PostgresVForce360Repository implements VForce360RepositoryPort {
    @Override
    public VForce360Aggregate load(String id) {
        return new VForce360Aggregate(id);
    }
    @Override
    public void save(VForce360Aggregate aggregate) {}
}
