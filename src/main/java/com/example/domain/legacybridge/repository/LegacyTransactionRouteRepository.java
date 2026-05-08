package com.example.domain.legacybridge.repository;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;

import java.util.Optional;

/**
 * Repository interface for LegacyTransactionRoute aggregates.
 * Maps to the concrete class name used in the domain package.
 */
public interface LegacyTransactionRouteRepository {
    LegacyTransactionRoute save(LegacyTransactionRoute aggregate);
    Optional<LegacyTransactionRoute> findById(String routeId);
}
