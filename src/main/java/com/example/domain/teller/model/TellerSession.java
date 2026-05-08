package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean isActive = false;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof StartSessionCmd c) {
            // Invariant: A teller must be authenticated to initiate a session.
            // (Implied by valid context logic or pre-conditions)
            // Invariant: Session cannot already be active (simplified check for this story)
            if (this.isActive) {
                throw new IllegalStateException("Session is already active for ID: " + c.sessionId());
            }
            
            // Invariant: Navigation state must accurately reflect context.
            // (Assuming valid terminalId/tellerId is sufficient for 'context' here)
            if (c.terminalId() == null || c.terminalId().isBlank()) {
                 throw new IllegalArgumentException("Terminal ID is required to start session.");
            }
            if (c.tellerId() == null || c.tellerId().isBlank()) {
                 throw new IllegalArgumentException("Teller ID is required to start session.");
            }

            var event = new SessionStartedEvent(c.sessionId(), c.tellerId(), c.terminalId(), Instant.now());
            this.isActive = true;
            addEvent(event);
            incrementVersion();
            return List.of(event);
        }
        throw new UnknownCommandException(cmd);
    }
}
