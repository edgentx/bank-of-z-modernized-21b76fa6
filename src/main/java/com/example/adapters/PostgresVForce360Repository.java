package com.example.adapters;

import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of VForce360Repository.
 * Placeholder for the actual Postgres implementation.
 */
@Repository
public class PostgresVForce360Repository implements VForce360Repository {

    private final Map<String, VForce360Aggregate> store = new HashMap<>();

    @Override
    public Optional<VForce360Aggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(VForce360Aggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
