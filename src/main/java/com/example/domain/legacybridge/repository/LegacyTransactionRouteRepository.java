package com.example.domain.legacybridge.repository;

import com.example.domain.legacybridge.model.LegacyTransactionRouteAggregate;

import java.util.Optional;

public interface LegacyTransactionRouteRepository {
    LegacyTransactionRouteAggregate save(LegacyTransactionRouteAggregate aggregate);
    Optional<LegacyTransactionRouteAggregate> findById(String id);
}
