package com.example.mocks;

import com.example.domain.uinavigation.model.ScreenMap;
import com.example.domain.uinavigation.repository.ScreenMapRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryScreenMapRepository implements ScreenMapRepository {
    private final Map<String, ScreenMap> store = new ConcurrentHashMap<>();

    @Override
    public ScreenMap save(ScreenMap aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<ScreenMap> findById(String screenId) {
        return Optional.ofNullable(store.get(screenId));
    }
}