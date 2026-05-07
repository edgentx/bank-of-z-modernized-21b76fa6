package com.example.mocks;

import com.example.domain.s1.model.S1Aggregate;
import com.example.domain.s1.repository.S1Repository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryS1Repository implements S1Repository {
    private final Map<String, S1Aggregate> store = new HashMap<>();

    @Override
    public void save(S1Aggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<S1Aggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}