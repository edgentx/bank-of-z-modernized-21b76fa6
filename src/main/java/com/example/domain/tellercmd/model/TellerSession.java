package com.example.domain.tellercmd.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Teller Session Aggregate (S-20)
 * Manages user-interface-navigation state and teller session lifecycle.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean isAuthenticated;
    private boolean isActive;
    private Instant lastActivityAt;
    private boolean isNavigationDirty;

    // Configuration: Sessions timeout after 15 minutes of inactivity
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now(); // Default to now for simplicity in tests
        // Default constructor implies an unauthenticated/initial state depending on context.
        // For S-20 scenarios, we might need specific state setup logic.
    }

    // Used for testing scenarios to set up specific violation states
    public void markAuthenticated() {
        this.isAuthenticated = true;
        this.isActive = true;
    }

    public void markTimedOut() {
        this.lastActivityAt = Instant.now().minus(Duration.ofMinutes(20));
        this.isAuthenticated = true; // Assume it was valid once
    }

    public void markNavigationDirty() {
        this.isNavigationDirty = true;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return handleEndSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleEndSession(EndSessionCmd cmd) {
        // 1. Invariant: A teller must be authenticated to initiate a session (and end it)
        // (Logic: If not authenticated, cannot operate session)
        if (!isAuthenticated) {
            throw new IllegalStateException("Teller must be authenticated.");
        }

        // 2. Invariant: Sessions must timeout after a configured period of inactivity.
        // (Logic: Check if last activity is too old)
        if (Instant.now().isAfter(lastActivityAt.plus(SESSION_TIMEOUT))) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // 3. Invariant: Navigation state must accurately reflect the current operational context.
        // (Logic: If navigation is desynchronized/dirty, reject action)
        if (isNavigationDirty) {
            throw new IllegalStateException("Navigation state is inconsistent with operational context.");
        }

        // Success Path
        var event = new SessionEndedEvent(sessionId, Instant.now());
        this.isActive = false;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Getters for testing verification if needed
    public boolean isActive() { return isActive; }
    public boolean isAuthenticated() { return isAuthenticated; }
}
