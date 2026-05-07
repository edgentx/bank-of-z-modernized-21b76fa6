package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean authenticated;
    private Instant lastActivityAt;
    private boolean active;
    private boolean navigationStateStale;

    // Invariant: Sessions must timeout after 30 minutes of inactivity
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.authenticated = false;
        this.lastActivityAt = Instant.now();
        this.active = false; // Session starts inactive until initiated
        this.navigationStateStale = false;
    }

    public void markAuthenticated() {
        this.authenticated = true;
        this.active = true;
        this.lastActivityAt = Instant.now();
    }

    public void markNavigationStateStale() {
        this.navigationStateStale = true;
    }

    public void simulateInactivity() {
        // Helper for testing to simulate time passing
        this.lastActivityAt = Instant.now().minus(SESSION_TIMEOUT.plusSeconds(1));
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return endSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // Invariant 1: A teller must be authenticated to initiate (and therefore end) a session.
        if (!authenticated) {
            throw new IllegalStateException("Cannot end session: Teller is not authenticated.");
        }

        // Invariant 2: Sessions must timeout after a configured period of inactivity.
        // Note: If a session has timed out, it is effectively dead. We might allow an explicit end
        // to clean up, but strictly speaking, operations on a timed-out session usually fail first.
        if (hasTimedOut()) {
            throw new IllegalStateException("Cannot end session: Session has timed out due to inactivity.");
        }

        // Invariant 3: Navigation state must accurately reflect the current operational context.
        if (navigationStateStale) {
            throw new IllegalStateException("Cannot end session: Navigation state is stale/invalid.");
        }

        // Business logic: Terminate session and clear state
        TellerSessionEndedEvent event = new TellerSessionEndedEvent(this.sessionId);
        this.active = false;
        this.authenticated = false; // Clear sensitive state
        // this.lastActivityAt = null; // Optional: clear timestamp

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean hasTimedOut() {
        if (lastActivityAt == null) return false;
        return ChronoUnit.MILLIS.between(lastActivityAt, Instant.now()) > SESSION_TIMEOUT.toMillis();
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public boolean isNavigationStateStale() {
        return navigationStateStale;
    }
}
