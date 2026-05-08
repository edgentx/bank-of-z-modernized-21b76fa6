package com.example.mocks;

import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.repository.ScreenMapRepository;
import java.util.HashMap;
import java.util.Map;

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
