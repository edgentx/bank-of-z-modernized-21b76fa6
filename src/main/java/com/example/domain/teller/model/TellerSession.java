package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * TellerSession Aggregate
 * Manages the state of a teller's interaction with the system.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean isAuthenticated = false;
    private boolean isTimedOut = false;
    private Instant lastActivityAt;
    private String currentNavigationState;

    // Constructor for a new aggregate
    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now(); // Defaults to now
        this.currentNavigationState = "HOME";
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
        // In this domain context, we assume the aggregate holds auth status.
        if (!isAuthenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (isTimedOut) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // Example check: Navigation state must be clean/home to start a new session logic, or valid.
        if (currentNavigationState == null || currentNavigationState.equals("INVALID")) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // Business Logic
        var event = new SessionStartedEvent(sessionId, cmd.tellerId(), cmd.terminalId(), Instant.now());
        
        // Apply state changes
        // this.isAuthenticated = true; // Already true
        this.lastActivityAt = event.occurredAt();
        this.currentNavigationState = "ACTIVE_SESSION";

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Package-private setters for testing violation scenarios
    void setUnauthenticated() {
        this.isAuthenticated = false;
    }

    void setTimedOut() {
        this.isTimedOut = true;
    }

    void setInvalidNavigationState() {
        this.currentNavigationState = "INVALID";
    }

    public boolean isSessionActive() {
        return isAuthenticated && !isTimedOut;
    }
}
