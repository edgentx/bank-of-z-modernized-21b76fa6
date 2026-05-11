package com.example.adapters;

import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-Memory implementation of the VForce360Repository.
 * In a real production environment, this would interact with DB2 via JPA/Hibernate.
 */
@Repository
public class PostgresVForce360Repository implements VForce360Repository {

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
