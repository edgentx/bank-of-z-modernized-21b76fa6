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
    private Instant lastActivityAt;
    private String navigationState;
    private boolean active;
    private final Duration TIMEOUT = Duration.ofMinutes(30); // Configured timeout period

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.navigationState = "IDLE"; // Valid operational context default
    }

    @Override
    public String id() {
        return sessionId;
    }

    // Domain logic helper to simulate state for testing
    public void start(String tellerId, Instant timestamp) {
        this.tellerId = tellerId;
        this.lastActivityAt = timestamp;
        this.active = true;
    }

    // Domain logic helper to simulate invalid state for testing
    public void forceNavigationState(String state) {
        this.navigationState = state;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return endSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // 1. Invariant: A teller must be authenticated to initiate a session.
        // We check if the tellerId is set, indicating a session was initiated.
        if (this.tellerId == null || !active) {
            throw new IllegalStateException("Teller must be authenticated to initiate a session.");
        }

        // 2. Invariant: Sessions must timeout after a configured period of inactivity.
        // For this command, we check if the session is already effectively timed out before ending.
        Instant now = Instant.now();
        if (this.lastActivityAt != null && Duration.between(this.lastActivityAt, now).compareTo(TIMEOUT) > 0) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // 3. Invariant: Navigation state must accurately reflect the current operational context.
        // If the state is invalid (e.g. stuck in a transaction or error), reject the end command.
        if (this.navigationState == null || !"IDLE".equals(this.navigationState)) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context (Expected IDLE).");
        }

        var event = new SessionEndedEvent(sessionId, now);
        this.active = false;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public String getTellerId() {
        return tellerId;
    }

    public boolean isActive() {
        return active;
    }
}
