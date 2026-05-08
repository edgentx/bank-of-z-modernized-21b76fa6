package com.example.mocks;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;

/**
 * In-memory repository for testing TellerSession.
 * Needed as S18 depends on TellerSessionRepository interface.
 */
public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    @Override
    public TellerSessionAggregate load(String id) {
        return null; // Simple mock
    }

    @Override
    public void save(TellerSessionAggregate aggregate) {
        // No-op for in-memory testing within step definitions
    }
}