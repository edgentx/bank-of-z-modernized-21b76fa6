package com.example.ports;

import com.example.domain.vforce360.model.VForce360Aggregate;

/**
 * Repository Port for VForce360 Aggregate.
 */
public interface VForce360RepositoryPort {
    VForce360Aggregate load(String id);
    void save(VForce360Aggregate aggregate);
}
