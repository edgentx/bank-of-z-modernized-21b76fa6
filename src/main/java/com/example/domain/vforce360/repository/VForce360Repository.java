package com.example.domain.vforce360.repository;

import com.example.domain.vforce360.model.VForce360Aggregate;

import java.util.Optional;

/**
 * Repository interface for VForce360 Aggregate.
 * Abstracts the persistence mechanism.
 */
public interface VForce360Repository {

    VForce360Aggregate save(VForce360Aggregate aggregate);

    Optional<VForce360Aggregate> findById(String id);

    VForce360Aggregate create();
}
