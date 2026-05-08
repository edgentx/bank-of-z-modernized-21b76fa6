package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * TellerSession aggregate
 * Handles teller authentication, state, and session lifecycle.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private boolean isAuthenticated;
    private boolean isActive;
    private Instant lastActivityAt;
    private Instant sessionTimeoutAt;
    private String currentScreen;

    // Configuration for session timeout (e.g., 15 minutes)
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.isAuthenticated = false;
        this.isActive = false;
        this.lastActivityAt = Instant.now();
        this.sessionTimeoutAt = this.lastActivityAt.plus(SESSION_TIMEOUT);
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd) {
            return endSession();
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession() {
        // Invariant Check: A teller must be authenticated to end a session.
        if (!isAuthenticated) {
            throw new IllegalStateException("Cannot end session: No authenticated teller found for session " + sessionId);
        }

        // Invariant Check: Sessions must timeout after a configured period of inactivity.
        if (Instant.now().isAfter(sessionTimeoutAt)) {
            throw new IllegalStateException("Cannot end session: Session has timed out due to inactivity.");
        }

        // Invariant Check: Navigation state must accurately reflect the current operational context.
        // For example: A session locked in a 'Critical Transaction' screen cannot be terminated normally.
        // (Simulated here by checking if the current screen is 'LOCKED')
        if ("LOCKED".equals(this.currentScreen)) {
            throw new IllegalStateException("Cannot end session: Navigation state is locked. Resolve context first.");
        }

        var event = new TellerSessionEndedEvent(this.sessionId, Instant.now());
        this.isActive = false;
        this.isAuthenticated = false; // Clear sensitive state
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Getters for testing and state access
    public boolean isActive() { return isActive; }
    public boolean isAuthenticated() { return isAuthenticated; }
    public Instant getSessionTimeoutAt() { return sessionTimeoutAt; }
    public String getCurrentScreen() { return currentScreen; }
    
    // Setters for test setup
    public void markAuthenticated(String tellerId) {
        this.tellerId = tellerId;
        this.isAuthenticated = true;
        this.isActive = true;
    }

    public void setSessionTimeoutAt(Instant instant) {
        this.sessionTimeoutAt = instant;
    }

    public void setCurrentScreen(String screen) {
        this.currentScreen = screen;
    }
}
