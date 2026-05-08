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
    private boolean timedOut = false;
    private boolean navStateValid = true;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
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
        // Invariant: Authentication
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Operational Context (Navigation State)
        if (!navStateValid) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // Invariant: Timeout
        if (timedOut) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        if (active) {
            throw new IllegalStateException("Session is already active.");
        }

        var event = new SessionStartedEvent(cmd.sessionId(), cmd.tellerId(), cmd.terminalId(), Instant.now());
        addEvent(event);
        incrementVersion();
        this.active = true;
        return List.of(event);
    }

    // Test helpers / State setters for validation simulation
    public void markAuthenticated() {
        this.authenticated = true;
    }

    public void markExpired() {
        this.timedOut = true;
    }

    public void corruptNavigationState() {
        this.navStateValid = false;
    }

    public boolean isActive() {
        return active;
    }
}
