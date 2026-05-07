package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * Teller Session Aggregate.
 * Manages the lifecycle of a teller's interaction with the system (user-interface-navigation).
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private boolean active;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.active = false;
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
        // Invariant: A teller must be authenticated to initiate a session.
        if (!cmd.isAuthenticated()) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // (Simulated check based on command flags for domain rule enforcement)
        if (cmd.isTimedOut()) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        if (cmd.isNavStateInvalid()) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // Business Logic
        if (active) {
            throw new IllegalStateException("Session already active for " + sessionId);
        }
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or empty");
        }
        if (cmd.terminalId() == null || cmd.terminalId().isBlank()) {
            throw new IllegalArgumentException("terminalId cannot be null or empty");
        }

        SessionStartedEvent event = new SessionStartedEvent(this.sessionId, cmd.tellerId(), cmd.terminalId(), Instant.now());
        this.tellerId = cmd.tellerId();
        this.terminalId = cmd.terminalId();
        this.active = true;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public boolean isActive() {
        return active;
    }

    public String getTellerId() {
        return tellerId;
    }
}
