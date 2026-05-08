package com.example.domain.teller.model;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.shared.Aggregate;

import java.time.Instant;
import java.util.UUID;

/**
 * Legacy Wrapper / facade for the TellerSessionAggregate.
 * S-18: Exposes the domain logic via legacy-compatible method names.
 */
public class TellerSession {
    private final TellerSessionAggregate aggregate;

    public TellerSession() {
        this.aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
    }

    public TellerSession(TellerSessionAggregate aggregate) {
        this.aggregate = aggregate;
    }

    public String getId() {
        return aggregate.id();
    }

    public boolean isActive() {
        return aggregate.isActive();
    }

    public void markAuthenticated() {
        aggregate.markAuthenticated();
    }

    public void startSession(String tellerId, String terminalId) {
        // Bridge legacy method to new Command pattern
        var cmd = new com.example.domain.tellersession.model.StartSessionCmd(aggregate.id(), tellerId, terminalId);
        aggregate.execute(cmd);
    }

    // Expose underlying aggregate for repository persistence
    public TellerSessionAggregate getAggregate() {
        return aggregate;
    }
}
