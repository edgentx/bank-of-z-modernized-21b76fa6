package com.example.domain.screenmap.repository;

import com.example.domain.navigation.model.ScreenMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryScreenMapRepository implements ScreenMapRepository {

    private final Map<String, ScreenMap> store = new ConcurrentHashMap<>();

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
