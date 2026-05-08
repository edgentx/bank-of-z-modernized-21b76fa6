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
     * 1. Teller must be authenticated (simulated via command context or aggregate state).
     * 2. Navigation state (initialContext) must be valid.
     * 3. Session must not have timed out (relevant if resuming, though typically start is fresh).
     */
    private List<DomainEvent> startSession(StartSessionCmd cmd) {
        // Invariant: Teller must be authenticated.
        // Assuming the command carries the identity of the authenticated teller.
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("Teller must be authenticated to initiate a session.");
        }
        this.isAuthenticated = true;

        // Invariant: Sessions must timeout after a configured period.
        // We check configuration availability here.
        if (!this.timeoutConfigured) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // Assuming 'initialContext' represents the initial screen/flow.
        if (cmd.initialContext() == null || cmd.initialContext().isBlank()) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context.");
        }

        // Business Logic Check: Prevent restarting an active session if that's an invariant,
        // though the prompt implies "Initiates a teller session".
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

    // Method to help set up test cases for invariants if needed via reflection or package-private access
    protected void markTimeoutUnconfigured() {
        this.timeoutConfigured = false;
    }
    protected void activate() {
        this.isActive = true;
        this.isAuthenticated = true;
    }
}
