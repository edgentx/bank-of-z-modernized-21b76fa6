package com.example.mocks;

import com.example.domain.ui.model.ScreenMapAggregate;
import com.example.domain.ui.repository.ScreenMapRepository;

import java.util.HashMap;
import java.util.Map;

public class ScreenMapRepositoryMock implements ScreenMapRepository {
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
