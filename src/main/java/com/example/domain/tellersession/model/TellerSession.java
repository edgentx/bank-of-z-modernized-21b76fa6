package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private boolean isAuthenticated;
    private Instant lastActivityAt;
    private boolean isActive;
    private boolean isNavigable;

    // Configuration for timeout (e.g., 15 minutes)
    private static final long SESSION_TIMEOUT_MINUTES = 15;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
        this.isActive = true;
        this.isNavigable = true;
    }

    public void markAuthenticated(String tellerId) {
        this.tellerId = tellerId;
        this.isAuthenticated = true;
    }

    public void markUnauthenticated() {
        this.isAuthenticated = false;
    }

    public void markStale() {
        this.lastActivityAt = Instant.now().minus(SESSION_TIMEOUT_MINUTES + 1, ChronoUnit.MINUTES);
    }

    public void markNavigationalInvalid() {
        this.isNavigable = false;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd) {
            return handle((EndSessionCmd) cmd);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handle(EndSessionCmd cmd) {
        // Invariant: Teller must be authenticated
        if (!isAuthenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Session must not have timed out
        if (hasTimedOut()) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must be valid
        if (!isNavigable) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        SessionEndedEvent event = new SessionEndedEvent(this.sessionId, Instant.now());
        this.isActive = false;
        // Clear sensitive state implicitly by marking inactive
        this.tellerId = null; 
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean hasTimedOut() {
        Instant now = Instant.now();
        long minutesSinceActivity = ChronoUnit.MINUTES.between(lastActivityAt, now);
        return minutesSinceActivity > SESSION_TIMEOUT_MINUTES;
    }
}
