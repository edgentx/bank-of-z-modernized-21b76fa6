package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * TellerSession Aggregate.
 * Represents a bank teller's authenticated state at a specific physical terminal.
 * Handles navigation and session lifecycle invariants.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private boolean active;
    private Instant lastActivityAt;
    private String navigationState;

    // Invariant constants (could be externalized later)
    private static final long SESSION_TIMEOUT_MINUTES = 30;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.active = false;
        this.navigationState = "HOME"; // Default operational context
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
            throw new IllegalStateException("Teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // (N/A for new session starts, checked on command loading in real app, valid here for state transitions)

        // Invariant: Navigation state must accurately reflect the current operational context.
        // Assuming for Start Cmd, we ensure we aren't hijacking a weird state.
        if (this.active) {
            throw new IllegalStateException("Session already active for " + this.sessionId);
        }

        // Validate inputs
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be blank");
        }
        if (cmd.terminalId() == null || cmd.terminalId().isBlank()) {
            throw new IllegalArgumentException("terminalId cannot be blank");
        }

        var event = new SessionStartedEvent(this.sessionId, cmd.tellerId(), cmd.terminalId());

        // Apply state changes
        this.tellerId = cmd.tellerId();
        this.terminalId = cmd.terminalId();
        this.active = true;
        this.lastActivityAt = Instant.now();
        this.navigationState = "TELLER_MENU";

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Getters for testing/verification
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public boolean isActive() { return active; }
    public Instant getLastActivityAt() { return lastActivityAt; }
    public String getNavigationState() { return navigationState; }
}
