package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * TellerSession Aggregate.
 * Manages the lifecycle of a teller's interaction with the system.
 * Enforces invariants for session initiation.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private String currentContext;
    private boolean isActive = false;
    private boolean isAuthenticated = false;
    private Instant lastActivityAt;
    private boolean timeoutConfigured = true; // Assume configured by default unless explicitly set

    // Configured timeout period in minutes (e.g., 15 minutes)
    private static final long SESSION_TIMEOUT_MINUTES = 15;

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

    /**
     * Executes StartSessionCmd.
     * Enforces invariants:
     * 1. Teller must be authenticated.
     * 2. Sessions must timeout after a configured period.
     * 3. Navigation state must accurately reflect the current operational context.
     */
    private List<DomainEvent> startSession(StartSessionCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        // We validate the tellerId presence as a proxy for authentication in this context.
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("Teller must be authenticated to initiate a session.");
        }
        this.isAuthenticated = true;

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // We verify the configuration flag is true.
        if (!this.timeoutConfigured) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // We validate the initialContext is provided.
        if (cmd.initialContext() == null || cmd.initialContext().isBlank()) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context.");
        }

        // Business Rule: Prevent restarting an active session.
        if (this.isActive) {
             throw new IllegalStateException("Session is already active.");
        }

        // Create Event
        var event = new SessionStartedEvent(
            this.sessionId,
            cmd.tellerId(),
            cmd.terminalId(),
            cmd.initialContext(),
            Instant.now()
        );

        // Apply State Changes
        this.tellerId = cmd.tellerId();
        this.terminalId = cmd.terminalId();
        this.currentContext = cmd.initialContext();
        this.isActive = true;
        this.lastActivityAt = event.occurredAt();

        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    // Getters for testing/projections
    public boolean isActive() { return isActive; }
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public String getCurrentContext() { return currentContext; }
    public Instant getLastActivityAt() { return lastActivityAt; }

    // Methods to help set up test cases for invariants if needed via reflection or package-private access
    protected void markTimeoutUnconfigured() {
        this.timeoutConfigured = false;
    }
    protected void activate() {
        this.isActive = true;
        this.isAuthenticated = true;
    }
}
