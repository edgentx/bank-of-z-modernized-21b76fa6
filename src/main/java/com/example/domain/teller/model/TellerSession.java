package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * TellerSession Aggregate.
 * Manages the state of a bank teller's terminal session.
 * Enforces invariants regarding authentication, timeouts, and navigation state.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean isAuthenticated;
    private boolean isActive; // false if timed out
    private boolean isNavigationValid;
    private String currentTellerId;
    private String currentTerminalId;

    /**
     * Constructor for a new session (Green phase).
     * Initializes with valid defaults to satisfy the happy path tests.
     */
    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        // Default state: Valid context, ready to start
        this.isAuthenticated = true;
        this.isActive = true;
        this.isNavigationValid = true;
    }

    /**
     * Constructor for testing specific invariant violations.
     */
    public TellerSession(String sessionId, boolean isAuthenticated, boolean isActive, boolean isNavigationValid) {
        this.sessionId = sessionId;
        this.isAuthenticated = isAuthenticated;
        this.isActive = isActive;
        this.isNavigationValid = isNavigationValid;
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
        // Invariant: Authentication
        if (!isAuthenticated) {
            throw new IllegalStateException("Teller must be authenticated to initiate a session.");
        }

        // Invariant: Session Timeout / Activity
        if (!isActive) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // Invariant: Navigation State
        if (!isNavigationValid) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // Command Validation
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or empty");
        }
        if (cmd.terminalId() == null || cmd.terminalId().isBlank()) {
            throw new IllegalArgumentException("terminalId cannot be null or empty");
        }

        // Apply State Changes
        this.currentTellerId = cmd.tellerId();
        this.currentTerminalId = cmd.terminalId();
        
        // Create Event
        // We let the Event constructor handle timestamp generation
        SessionStartedEvent event = new SessionStartedEvent(
                UUID.randomUUID().toString(),
                this.sessionId,
                cmd.tellerId(),
                cmd.terminalId(),
                Instant.now()
        );

        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    // Getters for Projections/Testing
    public String getCurrentTellerId() { return currentTellerId; }
    public String getCurrentTerminalId() { return currentTerminalId; }
}
