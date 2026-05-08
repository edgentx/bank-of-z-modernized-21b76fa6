package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * TellerSession Aggregate
 * Handles teller login state, terminal binding, session lifecycle, and UI navigation context.
 */
public class TellerSession extends AggregateRoot {
    private final String sessionId;
    private boolean isAuthenticated = false;
    private boolean isTimedOut = false;
    private boolean navigationValid = true;

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
            return startSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> startSession(StartSessionCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        if (!isAuthenticated) {
            throw new IllegalStateException("Teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // (Simulated here by a flag set in test or logic handling timed out state)
        if (isTimedOut) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        if (!navigationValid) {
            throw new IllegalStateException("Navigation state is invalid for the current operational context.");
        }

        // Validate Payload
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("tellerId required");
        }
        if (cmd.terminalId() == null || cmd.terminalId().isBlank()) {
            throw new IllegalArgumentException("terminalId required");
        }

        var event = new SessionStartedEvent(sessionId, cmd.tellerId(), cmd.terminalId(), Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // --- Test/Data Manipulation Helpers ---
    
    public void markAuthenticated() {
        this.isAuthenticated = true;
    }

    public void simulateTimeoutViolation() {
        this.isTimedOut = true;
    }

    public void simulateNavigationViolation() {
        this.navigationValid = false;
    }
}
