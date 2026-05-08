package com.example.mocks;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of the LegacyTransactionRouteRepository for testing.
 */
public class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {

    private final Map<String, LegacyTransactionRoute> store = new HashMap<>();

    @Override
    public void save(LegacyTransactionRoute aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public LegacyTransactionRoute saveAndReturn(LegacyTransactionRoute aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<LegacyTransactionRoute> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
