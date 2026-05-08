package com.example.domain.vforce360.repository;

import com.example.domain.vforce360.model.VForce360Aggregate;

import java.util.Optional;

/**
 * Repository interface for VForce360 aggregates.
 */
public interface VForce360Repository {
    Optional<VForce360Aggregate> findById(String id);
    void save(VForce360Aggregate aggregate);
}
