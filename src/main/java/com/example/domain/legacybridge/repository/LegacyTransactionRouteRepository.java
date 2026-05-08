package com.example.domain.legacybridge.repository;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;

import java.util.Optional;

/**
 * Repository interface for LegacyTransactionRoute aggregates.
 */
public interface LegacyTransactionRouteRepository {
    void save(LegacyTransactionRoute aggregate);
    Optional<LegacyTransactionRoute> findById(String routeId);
}