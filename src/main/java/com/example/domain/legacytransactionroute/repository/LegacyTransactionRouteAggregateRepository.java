package com.example.domain.legacytransactionroute.repository;

import com.example.domain.legacytransactionroute.model.LegacyTransactionRouteAggregate;
import java.util.Optional;

/**
 * Domain port for the canonical {@link LegacyTransactionRouteAggregate}.
 * Distinct from {@code domain.legacy.repository.LegacyTransactionRouteRepository}
 * and {@code domain.legacybridge.repository.LegacyTransactionRouteRepository}
 * which both target older data-class variants of the same concept.
 */
public interface LegacyTransactionRouteAggregateRepository {
  Optional<LegacyTransactionRouteAggregate> findById(String routeId);
  void save(LegacyTransactionRouteAggregate aggregate);
}
