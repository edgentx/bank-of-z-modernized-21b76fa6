package com.example.domain.legacybridge.repository;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;

import java.util.Optional;

public interface LegacyTransactionRouteRepository {
    Optional<LegacyTransactionRoute> findById(String id);
    void save(LegacyTransactionRoute aggregate);
}
