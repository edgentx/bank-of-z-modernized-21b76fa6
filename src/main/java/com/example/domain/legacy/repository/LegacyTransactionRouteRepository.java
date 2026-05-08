package com.example.domain.legacy.repository;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import java.util.Optional;

/**
 * Repository interface for LegacyTransactionRoute aggregates.
 */
public interface LegacyTransactionRouteRepository {
    void save(LegacyTransactionRoute route);
    Optional<LegacyTransactionRoute> findById(String id);
}
