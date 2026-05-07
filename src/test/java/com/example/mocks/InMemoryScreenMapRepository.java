package com.example.mocks;

import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.repository.ScreenMapRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryScreenMapRepository implements ScreenMapRepository {

    private final Map<String, ScreenMapAggregate> store = new HashMap<>();

    @Override
    public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<ScreenMapAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public ScreenMapAggregate create(String id) {
        ScreenMapAggregate aggregate = new ScreenMapAggregate(id);
        store.put(id, aggregate);
        return aggregate;
    }
}