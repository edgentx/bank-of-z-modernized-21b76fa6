package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.util.List;

/**
 * TellerSession Aggregate.
 * Manages the lifecycle of a Bank Teller's terminal session.
 * Context: S-18 Implement StartSessionCmd.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private boolean active;
    private TellerSessionState navigationContext;

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
        // We enforce that the timeout configuration must be valid (positive).
        if (cmd.timeoutInSeconds() <= 0) {
            throw new IllegalArgumentException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // We enforce that a navigation state (context) must be provided.
        if (cmd.navigationContext() == null) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // Check idempotency or state constraint: Cannot start an already active session
        if (this.active) {
            throw new IllegalStateException("Session is already active.");
        }

        var event = new SessionStartedEvent(
                cmd.sessionId(),
                cmd.tellerId(),
                cmd.terminalId(),
                cmd.navigationContext(),
                java.time.Instant.now()
        );

        // Apply state changes
        this.tellerId = cmd.tellerId();
        this.terminalId = cmd.terminalId();
        this.active = true;
        this.navigationContext = cmd.navigationContext();

        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    // Accessors for testing/verification
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public boolean isActive() { return active; }
    public TellerSessionState getNavigationContext() { return navigationContext; }

}
