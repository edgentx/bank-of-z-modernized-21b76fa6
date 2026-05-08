package com.example.ports;

import com.example.domain.vforce360.model.VForce360Aggregate;

import java.util.Optional;

/**
 * Port interface for VForce360 Repository.
 */
public interface VForce360RepositoryPort {
    VForce360Aggregate save(VForce360Aggregate aggregate);
    Optional<VForce360Aggregate> findById(String id);
}