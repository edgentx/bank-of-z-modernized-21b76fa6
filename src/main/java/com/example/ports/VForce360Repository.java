package com.example.ports;

import com.example.domain.vforce360.model.VForce360Aggregate;

import java.util.Optional;

public interface VForce360Repository {
    VForce360Aggregate save(VForce360Aggregate aggregate);
    Optional<VForce360Aggregate> findById(String id);
}
