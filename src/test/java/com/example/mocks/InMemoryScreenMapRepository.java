package com.example.mocks;

import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.repository.ScreenMapRepository;

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
    public ScreenMapAggregate findById(String mapId) {
        return store.get(mapId);
    }
    
    public void clear() {
        store.clear();
    }
}