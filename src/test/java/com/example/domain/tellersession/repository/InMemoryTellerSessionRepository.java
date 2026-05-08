package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository {

    private final Map<String, TellerSession> store = new HashMap<>();

    public void save(TellerSession aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public TellerSession findById(String id) {
        return store.get(id);
    }
}
