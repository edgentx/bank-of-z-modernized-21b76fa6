package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Teller Session Aggregate (S-18).
 * Handles session lifecycle, timeouts, and navigation state invariants.
 */
public class TellerSession extends AggregateRoot {

    private String sessionId;
    private String terminalId;
    private boolean active;

    public TellerSession(String sessionId, String terminalId) {
        this.sessionId = sessionId;
        this.terminalId = terminalId;
        this.active = false;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof StartSessionCmd c) {
            return handleStartSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleStartSession(StartSessionCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        // The command carries the authentication context flag.
        if (!cmd.isAuthenticated()) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // Validate timeout is future-dated.
        if (cmd.sessionTimeoutAt() == null || cmd.sessionTimeoutAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // Validate state is provided and non-blank.
        if (cmd.expectedNavigationState() == null || cmd.expectedNavigationState().isBlank()) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context.");
        }

        var event = new SessionStartedEvent(
                cmd.aggregateId(),
                cmd.terminalId(),
                cmd.sessionTimeoutAt(),
                cmd.expectedNavigationState(),
                Instant.now()
        );

        // Apply state changes locally
        this.active = true;
        this.terminalId = cmd.terminalId();
        // Assuming we don't store the full timeout logic in the aggregate memory for this simple scope,
        // but the event is the source of truth.

        addEvent(event);
        incrementVersion();
        return Collections.singletonList(event);
    }

    public boolean isActive() {
        return active;
    }

    public String getTerminalId() {
        return terminalId;
    }
}
