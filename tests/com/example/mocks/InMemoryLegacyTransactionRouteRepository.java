package com.example.mocks;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {
    private final Map<String, LegacyTransactionRoute> store = new HashMap<>();

    @Override
    public Optional<LegacyTransactionRoute> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(LegacyTransactionRoute aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
