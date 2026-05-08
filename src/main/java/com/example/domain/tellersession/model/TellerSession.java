package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean isAuthenticated = true; // Default true for valid sessions
    private boolean isTimedOut = false;
    private boolean isNavigationStateValid = true; // Default true for valid sessions
    private boolean isActive = false;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
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
        // Invariant: A teller must be authenticated to initiate a session.
        // Context: The prompt says "EndSessionCmd rejected — A teller must be authenticated to initiate a session."
        // This implies that ending a session requires authentication (integrity).
        if (!isAuthenticated) {
            throw new IllegalStateException("Teller must be authenticated to end session");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (isTimedOut) {
            throw new IllegalStateException("Session already timed out");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        if (!isNavigationStateValid) {
            throw new IllegalStateException("Navigation state invalid");
        }

        var event = new SessionEndedEvent(sessionId);
        addEvent(event);
        incrementVersion();
        this.isActive = false;
        return List.of(event);
    }

    // --- Test Helper Methods (Setters to manipulate state for BDD testing) ---
    // In a real system, state is built from events. For this isolated domain unit test,
    // we allow direct state manipulation to simulate 'Given' clauses.

    public void setAuthenticated(boolean authenticated) {
        this.isAuthenticated = authenticated;
    }

    public void setTimedOut(boolean timedOut) {
        this.isTimedOut = timedOut;
    }

    public void setNavigationStateValid(boolean valid) {
        this.isNavigationStateValid = valid;
    }

    public boolean isActive() {
        return isActive;
    }
}
