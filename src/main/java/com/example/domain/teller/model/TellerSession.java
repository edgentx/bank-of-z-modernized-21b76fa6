package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.util.List;

/**
 * Teller Session Aggregate.
 * Handles session lifecycle, authentication, and UI navigation state.
 * S-18: user-interface-navigation
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private String navigationContext;
    private int timeoutInSeconds;
    private boolean active;

    // Maximum allowed timeout in seconds (e.g., 30 minutes)
    private static final int MAX_TIMEOUT_SECONDS = 1800;

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
        // Invariant 1: A teller must be authenticated to initiate a session.
        if (!cmd.isAuthenticated()) {
            throw new IllegalArgumentException("A teller must be authenticated to initiate a session.");
        }

        // Invariant 2: Sessions must timeout after a configured period of inactivity.
        if (cmd.timeoutInSeconds() <= 0 || cmd.timeoutInSeconds() > MAX_TIMEOUT_SECONDS) {
            throw new IllegalArgumentException("Sessions must timeout after a configured period of inactivity. Timeout must be between 1 and " + MAX_TIMEOUT_SECONDS + " seconds.");
        }

        // Invariant 3: Navigation state must accurately reflect the current operational context.
        if (cmd.navigationContext() == null || cmd.navigationContext().isBlank()) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context. Context cannot be blank.");
        }

        // Apply state changes
        this.tellerId = cmd.tellerId();
        this.terminalId = cmd.terminalId();
        this.navigationContext = cmd.navigationContext();
        this.timeoutInSeconds = cmd.timeoutInSeconds();
        this.active = true;

        // Create event
        var event = new SessionStartedEvent(
                this.sessionId,
                this.tellerId,
                this.terminalId,
                this.navigationContext,
                cmd.occurredAt()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Getters for testing/verification
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public String getNavigationContext() { return navigationContext; }
    public int getTimeoutInSeconds() { return timeoutInSeconds; }
    public boolean isActive() { return active; }
}
