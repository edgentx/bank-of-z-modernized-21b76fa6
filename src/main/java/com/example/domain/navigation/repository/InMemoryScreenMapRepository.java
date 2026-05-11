package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryScreenMapRepository implements ScreenMapRepository {
    private final Map<String, ScreenMap> store = new HashMap<>();

    @Override
    public Optional<ScreenMap> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(ScreenMap aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
