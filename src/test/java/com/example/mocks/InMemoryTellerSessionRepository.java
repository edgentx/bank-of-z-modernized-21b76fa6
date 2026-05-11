package com.example.mocks;

import com.example.domain.tellermessaging.model.TellerSession;
import com.example.domain.tellermessaging.repository.TellerSessionRepository;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSession> store = new HashMap<>();

    @Override
    public void save(TellerSession session) {
        store.put(session.id(), session);
    }

    @Override
    public TellerSession load(String id) {
        TellerSession s = store.get(id);
        if (s == null) throw new RuntimeException("Session not found: " + id);
        return s;
    }
}
