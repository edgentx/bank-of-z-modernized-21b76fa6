package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean started;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.started = false;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof StartSessionCmd c) {
            return startSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> startSession(StartSessionCmd cmd) {
        if (started) {
            throw new IllegalStateException("Session already started: " + sessionId);
        }
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("tellerId required");
        }
        if (cmd.terminalId() == null || cmd.terminalId().isBlank()) {
            throw new IllegalArgumentException("terminalId required");
        }

        // Placeholder for Invariants:
        // A teller must be authenticated to initiate a session.
        // Sessions must timeout after a configured period of inactivity.
        // Navigation state must accurately reflect the current operational context.

        var event = new SessionStartedEvent(sessionId, cmd.tellerId(), cmd.terminalId(), Instant.now());
        this.started = true;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Test helper to simulate state for negative scenarios
    public void markAsStarted() {
        this.started = true;
    }

    public boolean isStarted() {
        return started;
    }
}
