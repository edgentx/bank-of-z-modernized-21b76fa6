package com.example.domain.navigation.model;

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
    private boolean authenticated;
    private boolean active;
    private Instant lastActivityAt;
    private Instant sessionStart;
    private Duration timeoutDuration;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.timeoutDuration = Duration.ofMinutes(30); // Default timeout config
    }

    @Override
    public String id() {
        return sessionId;
    }

    public void init(String tellerId, Instant startTime) {
        this.tellerId = tellerId;
        this.sessionStart = startTime;
        this.lastActivityAt = startTime;
        this.authenticated = true;
        this.active = true;
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
        // (Interpreted as: Only an authenticated teller can end their own session)
        if (!authenticated || !cmd.tellerId().equals(this.tellerId)) {
            throw new IllegalStateException("Teller must be authenticated to end the session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (hasTimedOut(Instant.now())) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // (Interpreted as: Cannot end a session that isn't active)
        if (!active) {
            throw new IllegalStateException("Session is not active and cannot be ended.");
        }

        SessionEndedEvent event = new SessionEndedEvent(this.id(), this.sessionId, Instant.now());
        this.active = false;
        this.authenticated = false;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public void markTimedOut() {
        this.active = false;
        this.authenticated = false;
    }

    private boolean hasTimedOut(Instant now) {
        return Duration.between(lastActivityAt, now).compareTo(timeoutDuration) > 0;
    }

    // Setters for test state manipulation
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setLastActivityAt(Instant lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }
    
    public void setTellerId(String tellerId) {
        this.tellerId = tellerId;
    }
}
