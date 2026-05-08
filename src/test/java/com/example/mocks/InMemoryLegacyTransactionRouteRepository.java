package com.example.mocks;

import com.example.domain.legacy.model.LegacyTransactionRouteAggregate;
import com.example.domain.legacy.repository.LegacyTransactionRouteRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {

    private final Map<String, LegacyTransactionRouteAggregate> store = new HashMap<>();

    @Override
    public void save(LegacyTransactionRouteAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<LegacyTransactionRouteAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public LegacyTransactionRouteAggregate create(String id) {
        var aggregate = new LegacyTransactionRouteAggregate(id);
        store.put(id, aggregate);
        return aggregate;
    }
}