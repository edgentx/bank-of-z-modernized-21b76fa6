package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.command.NavigateMenuCmd;
import com.example.domain.tellersession.event.MenuNavigatedEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {
    private final String sessionId;
    private String tellerId;
    private String currentMenuId;
    private boolean authenticated = false;
    private Instant lastActivityAt;
    private Instant sessionStart;
    private Duration timeoutDuration = Duration.ofMinutes(30);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
        this.sessionStart = Instant.now();
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof NavigateMenuCmd c) {
            return navigate(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> navigate(NavigateMenuCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        // Assuming navigation requires authentication.
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (hasTimedOut()) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        if (cmd.targetMenuId() == null || cmd.targetMenuId().isBlank()) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context: Invalid target menu.");
        }

        // Update State
        this.currentMenuId = cmd.targetMenuId();
        this.lastActivityAt = Instant.now();

        var event = new MenuNavigatedEvent(sessionId, cmd.targetMenuId(), cmd.action(), Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean hasTimedOut() {
        return Duration.between(lastActivityAt, Instant.now()).compareTo(timeoutDuration) > 0;
    }

    // Test Fixtures / Public Setters
    public void markAuthenticated(String tellerId) {
        this.tellerId = tellerId;
        this.authenticated = true;
    }

    public void markExpired() {
        this.lastActivityAt = Instant.now().minus(timeoutDuration.plusSeconds(1));
    }
}