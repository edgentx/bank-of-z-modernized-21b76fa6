package com.example.domain.vforce360.repository;

import com.example.domain.vforce360.model.VForce360Aggregate;

/**
 * Repository interface for VForce360 Aggregate.
 * Abstraction for persistence and event publishing.
 */
public interface VForce360Repository {
    void save(VForce360Aggregate aggregate);
    VForce360Aggregate findById(String id);
}