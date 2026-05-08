package com.example.domain.legacy.repository;

import com.example.domain.legacy.model.LegacyTransactionRouteAggregate;

import java.util.Optional;

public interface LegacyTransactionRouteRepository {
    void save(LegacyTransactionRouteAggregate aggregate);
    Optional<LegacyTransactionRouteAggregate> findById(String id);
    LegacyTransactionRouteAggregate create(String id);
}