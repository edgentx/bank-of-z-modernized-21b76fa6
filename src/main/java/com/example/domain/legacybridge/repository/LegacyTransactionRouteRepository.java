package com.example.domain.legacybridge.repository;

import com.example.domain.legacybridge.model.LegacyTransactionRouteAggregate;

import java.util.Optional;

/**
 * Repository for LegacyTransactionRouteAggregate.
 * Story S-23.
 */
public interface LegacyTransactionRouteRepository {
    void save(LegacyTransactionRouteAggregate aggregate);
    Optional<LegacyTransactionRouteAggregate> findById(String id);
}
