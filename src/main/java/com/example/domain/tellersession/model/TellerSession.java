package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * TellerSession aggregate (start).
 * BANK S-18
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean active = false;
    private boolean authenticated = false; // Invariant: must be true to start

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

    private List<DomainEvent> startSession(StartSessionCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // Assuming validation logic regarding timeout configuration exists here.
        if (cmd.sessionTimeoutAt() == null || cmd.sessionTimeoutAt().isBefore(Instant.now())) {
             throw new IllegalArgumentException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        if (cmd.expectedNavigationState() == null || cmd.expectedNavigationState().isBlank()) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context.");
        }

        if (active) {
            throw new IllegalStateException("Session already active: " + sessionId);
        }

        var event = new SessionStartedEvent(
            this.sessionId,
            cmd.tellerId(),
            cmd.terminalId(),
            cmd.expectedNavigationState(),
            cmd.sessionTimeoutAt(),
            Instant.now()
        );

        this.active = true;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Setters for test setup
    public void markAuthenticated() { this.authenticated = true; }
    public void markUnauthenticated() { this.authenticated = false; }
    public boolean isActive() { return active; }
}