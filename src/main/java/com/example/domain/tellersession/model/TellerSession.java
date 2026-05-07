package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * TellerSession Aggregate
 * Handles teller login sessions, navigation context, and timeouts.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean active;
    private String currentTellerId;

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
        // We assume null timeout or past timeout implies invalid config for starting a session.
        if (cmd.sessionTimeoutAt() == null || cmd.sessionTimeoutAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // Assuming "INIT" is the only valid starting state for this BDD context.
        if (cmd.expectedNavigationState() == null || !cmd.expectedNavigationState().equals("INIT")) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context.");
        }

        var event = new SessionStartedEvent(
                cmd.aggregateId(),
                cmd.tellerId(),
                cmd.terminalId(),
                cmd.sessionTimeoutAt(),
                cmd.expectedNavigationState(),
                Instant.now()
        );

        this.active = true;
        this.currentTellerId = cmd.tellerId();
        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    public boolean isActive() {
        return active;
    }

    public String getCurrentTellerId() {
        return currentTellerId;
    }
}
