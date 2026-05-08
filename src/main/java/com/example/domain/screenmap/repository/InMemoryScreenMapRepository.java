package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;

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
}
