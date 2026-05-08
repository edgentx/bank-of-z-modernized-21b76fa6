package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMapAggregate;

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
    public Optional<ScreenMapAggregate> findById(String screenId) {
        return Optional.ofNullable(store.get(screenId));
    }
}
