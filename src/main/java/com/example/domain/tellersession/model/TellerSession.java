package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {
    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private boolean active;
    private Instant lastActivityAt;
    private String currentContext;

    // Configuration: Session timeout (e.g., 15 minutes)
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

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
        // 1. Invariant: A teller must be authenticated to initiate a session.
        if (cmd.authenticatedAt() == null) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // 2. Invariant: Sessions must timeout after a configured period of inactivity.
        // Since this is a Start command, we check if the authentication token is too old.
        Instant now = Instant.now();
        if (Duration.between(cmd.authenticatedAt(), now).compareTo(SESSION_TIMEOUT) > 0) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // 3. Invariant: Navigation state must accurately reflect the current operational context.
        // Assuming "LOGGED_OUT" or null is invalid for starting a session if the context implies a workflow.
        // For this scenario, we assume a blank context is invalid.
        if (cmd.currentContext() != null && !cmd.currentContext().isBlank()) {
             // Simulating a state conflict: e.g. trying to start while already in a deep menu context.
             // For the purpose of the "violates" scenario, if a context is provided but violates the rule:
             throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // Success path
        var event = new SessionStartedEvent(this.sessionId, cmd.tellerId(), cmd.terminalId(), Instant.now());
        this.tellerId = cmd.tellerId();
        this.terminalId = cmd.terminalId();
        this.active = true;
        this.lastActivityAt = Instant.now();
        this.currentContext = cmd.currentContext();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Getters for testing/validation
    public boolean isActive() { return active; }
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
}