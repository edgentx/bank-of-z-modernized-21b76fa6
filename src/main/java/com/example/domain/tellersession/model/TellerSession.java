package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {

    public enum TellerSessionState {
        AUTHENTICATED,
        UNAUTHENTICATED
    }

    private final String sessionId;
    private boolean isActive = false;
    private boolean isAuthenticated = false;
    private Instant lastActivityAt;
    private boolean navigationContextValid = true;
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
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
        // Invariant: Authenticated check
        // Note: In real world, this might be checked differently, but adhering to story:
        if (!isAuthenticated) {
            throw new IllegalStateException("Teller must be authenticated to initiate a session.");
        }

        // Invariant: Timeout check
        if (Duration.between(lastActivityAt, Instant.now()).compareTo(SESSION_TIMEOUT) > 0) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state
        if (!navigationContextValid) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // Apply Event
        var event = new SessionEndedEvent(sessionId, Instant.now());
        addEvent(event);
        incrementVersion();

        // Update internal state
        this.isActive = false;
        this.isAuthenticated = false;

        return List.of(event);
    }

    // TEST SUPPORT METHODS
    // These methods are used to simulate internal state hydration for BDD tests
    // without needing a full EventSourcing repository setup or constructors that bypass business logic.
    
    public void enforceTestState(TellerSessionState state, Instant lastActivity, boolean navValid) {
        this.isAuthenticated = (state == TellerSessionState.AUTHENTICATED);
        this.isActive = this.isAuthenticated; // Assuming active if authenticated
        this.lastActivityAt = lastActivity;
        this.navigationContextValid = navValid;
    }

    public void flagNavigationStateInconsistent() {
        this.navigationContextValid = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}
