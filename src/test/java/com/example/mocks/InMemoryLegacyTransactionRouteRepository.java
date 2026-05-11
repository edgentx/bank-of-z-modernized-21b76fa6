package com.example.mocks;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.repository.LegacyTransactionRouteRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory mock adapter for LegacyTransactionRouteRepository.
 * Used exclusively in tests to avoid external dependencies (DB2/MongoDB).
 */
public class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {

    private final Map<String, LegacyTransactionRoute> store = new HashMap<>();

    @Override
    public void save(LegacyTransactionRoute aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<LegacyTransactionRoute> findById(String routeId) {
        return Optional.ofNullable(store.get(routeId));
    }
}
