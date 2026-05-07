package com.example.mocks;

import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSession> store = new HashMap<>();

    @Override
    public TellerSession save(TellerSession aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public TellerSession load(String id) {
        return store.get(id);
    }
}