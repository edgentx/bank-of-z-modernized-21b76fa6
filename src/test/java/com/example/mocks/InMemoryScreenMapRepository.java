package com.example.mocks;

import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.repository.ScreenMapRepository;

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
    public ScreenMapAggregate findById(String id) {
        return store.get(id);
    }
}
