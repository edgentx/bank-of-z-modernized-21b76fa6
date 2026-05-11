package com.example.mocks;

import com.example.domain.shared.Aggregate;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.repository.ScreenMapRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryScreenMapRepository implements ScreenMapRepository {

    private final Map<String, Aggregate> store = new HashMap<>();

    public InMemoryScreenMapRepository() {
        // Seed with a valid aggregate for the happy path
        store.put("LOGIN_SCR_01", new ScreenMapAggregate("LOGIN_SCR_01"));
    }

    @Override
    public Aggregate findById(String id) {
        // Return the aggregate instance or null
        return store.get(id);
    }

    @Override
    public void save(Aggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
