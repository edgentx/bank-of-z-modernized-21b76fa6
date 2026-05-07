package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * Teller Session Aggregate.
 * Handles session lifecycle, authentication context, and navigation state.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private boolean active = false;
    private boolean authenticated = false;
    private Instant lastActivityAt;
    private String currentContext; // Navigation state (e.g. 'DASHBOARD', 'CASH_WITHDRAWAL')

    // Configuration: Session timeout in minutes
    private static final long SESSION_TIMEOUT_MINUTES = 30;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.currentContext = "HOME"; // Default context
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
        // 1. Invariant: A teller must be authenticated to initiate a session.
        if (!cmd.isAuthenticated()) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // 2. Invariant: Sessions must timeout after a configured period of inactivity.
        if (isActiveAndTimedOut()) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // 3. Invariant: Navigation state must accurately reflect the current operational context.
        if (isInvalidContext()) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // If session is already active, potentially reject or refresh? Assuming restart or strict fresh start based on errors.
        if (this.active) {
             throw new IllegalStateException("Session is already active.");
        }

        Instant now = Instant.now();
        var event = new SessionStartedEvent(this.sessionId, cmd.tellerId(), cmd.terminalId(), now);

        // Apply state changes
        this.tellerId = cmd.tellerId();
        this.terminalId = cmd.terminalId();
        this.active = true;
        this.authenticated = true;
        this.lastActivityAt = now;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean isActiveAndTimedOut() {
        if (!active || lastActivityAt == null) return false;
        return lastActivityAt.plusSeconds(SESSION_TIMEOUT_MINUTES * 60).isBefore(Instant.now());
    }

    private boolean isInvalidContext() {
        // Mock validation: Context cannot be null or empty for an active session
        return this.currentContext == null || this.currentContext.isBlank();
    }

    // Getters for testing/verification
    public boolean isActive() { return active; }
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
}
