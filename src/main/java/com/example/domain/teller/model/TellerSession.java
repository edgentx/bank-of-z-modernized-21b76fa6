package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Teller Session Aggregate
 * S-18: Implement StartSessionCmd
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private boolean active;
    private Instant lastActivityAt;
    private String navigationState;

    // Invariant: Sessions must timeout after a configured period of inactivity.
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.active = false;
        this.lastActivityAt = Instant.now();
        this.navigationState = "HOME";
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
        if (!cmd.isAuthenticated()) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // (Check if the session being reused is too old, though for a new session this is usually relevant only if resurrecting)
        if (active && Duration.between(lastActivityAt, Instant.now()).compareTo(SESSION_TIMEOUT) > 0) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // For this scenario, we assume 'HOME' is the valid start context.
        if (this.navigationState == null || !this.navigationState.equals("HOME")) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // Logic for start
        this.tellerId = cmd.tellerId();
        this.terminalId = cmd.terminalId();
        this.active = true;
        this.lastActivityAt = Instant.now();

        var event = new SessionStartedEvent(sessionId, cmd.tellerId(), cmd.terminalId(), Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public boolean isActive() {
        return active;
    }

    public String getTellerId() {
        return tellerId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    // Used for testing violations
    public void markStale() {
        this.lastActivityAt = Instant.now().minus(SESSION_TIMEOUT).minusSeconds(10);
    }

    public void corruptNavigationState() {
        this.navigationState = "INVALID_STATE";
    }
}
