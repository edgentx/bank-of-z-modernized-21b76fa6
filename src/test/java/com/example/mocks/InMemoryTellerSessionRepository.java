package com.example.mocks;

import com.example.domain.teller.model.TellerSession;
import com.example.domain.teller.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSession> store = new HashMap<>();

    @Override
    public TellerSession save(TellerSession aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<TellerSession> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
