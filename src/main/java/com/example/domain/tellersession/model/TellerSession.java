package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * TellerSession Aggregate.
 * Manages the state of a teller's interaction with the terminal system,
 * handling authentication, context navigation, and session lifecycle.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private boolean active;
    private Instant lastActivityAt;
    private String currentContext;

    // Configuration constants (in a real app, these might be injected or static config)
    private static final long SESSION_TIMEOUT_MINUTES = 15;

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
            throw new IllegalArgumentException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // (Check relevant if this were a resume/reconnect command, or checking token freshness).
        // Here we assume if the command timestamp is too old relative to now, we reject.
        // For the purpose of this command, we verify the provided timestamp is reasonably recent,
        // or we rely on the caller to provide valid credentials. 
        // To satisfy the BDD scenario specifically for timeout violations:
        if (cmd.timestamp() != null && cmd.timestamp().isBefore(Instant.now().minusSeconds(SESSION_TIMEOUT_MINUTES * 60))) {
             throw new IllegalArgumentException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // We validate that the initial context provided is valid (e.g., not null/blank for a start).
        if (cmd.initialContext() == null || cmd.initialContext().isBlank()) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context.");
        }

        // Validate primitive fields
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("Teller ID is required");
        }
        if (cmd.terminalId() == null || cmd.terminalId().isBlank()) {
            throw new IllegalArgumentException("Terminal ID is required");
        }

        // Create Event
        Instant now = Instant.now();
        SessionStartedEvent event = new SessionStartedEvent(
                this.sessionId,
                cmd.tellerId(),
                cmd.terminalId(),
                cmd.initialContext(),
                now
        );

        // Apply State Changes
        this.tellerId = cmd.tellerId();
        this.terminalId = cmd.terminalId();
        this.active = true;
        this.currentContext = cmd.initialContext();
        this.lastActivityAt = now;

        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    // Getters for testing/verification
    public boolean isActive() { return active; }
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public String getCurrentContext() { return currentContext; }
    public Instant getLastActivityAt() { return lastActivityAt; }
}
