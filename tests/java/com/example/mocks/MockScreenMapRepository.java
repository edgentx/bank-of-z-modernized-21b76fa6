package com.example.mocks;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.repository.ScreenMapRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MockScreenMapRepository implements ScreenMapRepository {
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
