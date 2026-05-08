package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the repository for testing purposes.
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