package com.example.domain.tellersession.repository;

import com.example.domain.teller.model.TellerSession;
import com.example.domain.teller.repository.TellerSessionRepository;

/**
 * Adapter implementing the TellerSessionRepository interface.
 * Located in the 'tellersession' package structure as implied by the previous build errors,
 * but delegating to the shared repository contract.
 */
public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final java.util.Map<String, TellerSession> store = new java.util.HashMap<>();

    @Override
    public TellerSession save(TellerSession aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public java.util.Optional<TellerSession> findById(String id) {
        return java.util.Optional.ofNullable(store.get(id));
    }
}
