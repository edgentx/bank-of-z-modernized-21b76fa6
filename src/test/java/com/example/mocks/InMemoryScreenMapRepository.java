package com.example.mocks;

import com.example.domain.userinterface.model.ScreenMap;
import com.example.domain.userinterface.repository.ScreenMapRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryScreenMapRepository implements ScreenMapRepository {
    private final Map<String, ScreenMap> store = new HashMap<>();

    @Override
    public void save(ScreenMap aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<ScreenMap> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
