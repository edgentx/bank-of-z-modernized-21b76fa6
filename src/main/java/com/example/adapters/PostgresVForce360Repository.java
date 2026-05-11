package com.example.adapters;

import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import org.springframework.stereotype.Component;

/**
 * Postgres implementation of VForce360Repository.
 * S-FB-1: Needed to satisfy compiler, acting as persistence adapter.
 */
@Component
public class PostgresVForce360Repository implements VForce360Repository {
    
    @Override
    public VForce360Aggregate load(String id) {
        // In a real scenario, this would hydrate from DB
        return new VForce360Aggregate(id);
    }

    @Override
    public void save(VForce360Aggregate aggregate) {
        // In a real scenario, this would persist to DB
    }
}
