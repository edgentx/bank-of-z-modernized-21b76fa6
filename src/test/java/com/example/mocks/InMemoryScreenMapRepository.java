package com.example.mocks;

import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.repository.ScreenMapRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryScreenMapRepository implements ScreenMapRepository {
    private final Map<String, ScreenMapAggregate> store = new HashMap<>();

    @Override
    public Optional<ScreenMapAggregate> findById(String screenId) {
        return Optional.ofNullable(store.get(screenId));
    }

    @Override
    public void save(ScreenMapAggregate aggregate) {
        // In memory, we just overwrite or put.
        // In a real repo, this would append events.
        store.put(aggregate.id(), aggregate);
    }
}
