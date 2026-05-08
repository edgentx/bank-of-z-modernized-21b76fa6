package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean authenticated = false;
    private boolean active = false;
    private Instant lastActivityAt;
    private String currentContext;

    // Configured timeout period in minutes (e.g., 15 minutes)
    private static final long TIMEOUT_MINUTES = 15;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
    }

    @Override
    public String id() {
        return sessionId;
    }

    public void markAuthenticated() {
        this.authenticated = true;
        this.active = true;
        this.lastActivityAt = Instant.now();
    }

    public void updateContext(String context) {
        this.currentContext = context;
        this.lastActivityAt = Instant.now();
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return endSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        if (!authenticated) {
            throw new IllegalStateException("Teller must be authenticated to end a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        Instant now = Instant.now();
        if (lastActivityAt != null && lastActivityAt.plusMillis(TIMEOUT_MINUTES * 60 * 1000).isBefore(now)) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // Here we interpret the violation as the system being in an invalid state (e.g., null context when active)
        // For the purpose of the test "violates Navigation state", we assume the aggregate sets a specific invalid flag or state.
        if (this.currentContext == null && this.active) {
             throw new IllegalStateException("Navigation state (context) is invalid or missing for active session.");
        }

        // If session is already ended (inactive), technically idempotent or error depending on spec. Assuming idempotent or valid transition.
        if (!active) {
             return List.of(); // Or throw error if strict state machine requires active->ended.
        }

        var event = new SessionEndedEvent(sessionId, Instant.now());
        this.active = false;
        // clear sensitive state
        this.authenticated = false;
        this.currentContext = null;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(Instant lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public String getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(String currentContext) {
        this.currentContext = currentContext;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
