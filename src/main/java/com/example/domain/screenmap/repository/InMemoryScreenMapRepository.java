package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryScreenMapRepository implements ScreenMapRepository {

    private final Map<String, ScreenMapAggregate> store = new ConcurrentHashMap<>();

    @Override
    public ScreenMapAggregate findById(String screenId) {
        return store.get(screenId);
    }

    @Override
    public void save(ScreenMapAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}