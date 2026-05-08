package com.example.domain.legacybridge.repository;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;

import java.util.Optional;

/**
 * Repository interface for LegacyTransactionRoute aggregates.
 */
public interface LegacyTransactionRouteRepository {
    void save(LegacyTransactionRoute aggregate); // Note: Changed return type to void to match Aggregate usage pattern usually, but interface must match impl.
    LegacyTransactionRoute saveAndReturn(LegacyTransactionRoute aggregate);
    Optional<LegacyTransactionRoute> findById(String id);
}
