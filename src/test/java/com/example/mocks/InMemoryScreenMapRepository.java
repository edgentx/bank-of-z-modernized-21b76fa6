package com.example.mocks;

import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.repository.ScreenMapRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryScreenMapRepository implements ScreenMapRepository {
    private final Map<String, ScreenMapAggregate> store = new HashMap<>();

    @Override
    public void save(ScreenMapAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<ScreenMapAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
