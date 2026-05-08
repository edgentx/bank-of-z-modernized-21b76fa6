package com.example.ports;

import com.example.domain.vforce360.model.VForce360Aggregate;

/**
 * Repository Port.
 */
public interface VForce360RepositoryPort {
    VForce360Aggregate load(String id);
    void save(VForce360Aggregate aggregate);
}
