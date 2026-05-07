package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMapAggregate;

import java.util.HashMap;
import java.util.Map;

public class InMemoryScreenMapRepository {
    private final Map<String, ScreenMapAggregate> store = new HashMap<>();

    public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    public ScreenMapAggregate findById(String id) {
        return store.get(id);
    }
}
