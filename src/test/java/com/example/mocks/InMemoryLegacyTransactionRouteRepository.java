package com.example.mocks;

import com.example.domain.legacybridge.model.LegacyTransactionRouteAggregate;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {

    private final Map<String, LegacyTransactionRouteAggregate> store = new ConcurrentHashMap<>();

    @Override
    public void save(LegacyTransactionRouteAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<LegacyTransactionRouteAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}