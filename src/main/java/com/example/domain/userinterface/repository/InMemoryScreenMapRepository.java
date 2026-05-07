package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.ScreenMap;

import java.util.HashMap;
import java.util.Map;

public class InMemoryScreenMapRepository implements ScreenMapRepository {
    private final Map<String, ScreenMap> store = new HashMap<>();

    @Override
    public ScreenMap save(ScreenMap aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public ScreenMap findById(String id) {
        return store.get(id);
    }
}
