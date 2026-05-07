package com.example.mocks;

import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSession> store = new HashMap<>();

    @Override
    public TellerSession save(TellerSession session) {
        store.put(session.id(), session);
        return session;
    }

    @Override
    public TellerSession findById(String id) {
        return store.get(id);
    }
}
