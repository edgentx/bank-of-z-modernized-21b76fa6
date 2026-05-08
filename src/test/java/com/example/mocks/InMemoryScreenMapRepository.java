package com.example.mocks;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.repository.ScreenMapRepository;

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
