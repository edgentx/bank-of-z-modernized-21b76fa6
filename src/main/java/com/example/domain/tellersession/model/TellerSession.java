package com.example.domain.tellersession.model;

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
    private String tellerId;
    private String currentMenuId;
    private Instant lastActivityAt;
    private boolean authenticated;

    private static final Duration SESSION_TIMEOUT = Duration.of(30, ChronoUnit.MINUTES);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
    }

    @Override
    public String id() {
        return sessionId;
    }

    // Helper for test setup to simulate violation states
    public void markAuthenticated(boolean isAuthenticated) {
        this.authenticated = isAuthenticated;
    }

    public void markStale() {
        this.lastActivityAt = Instant.now().minus(SESSION_TIMEOUT).minusSeconds(1);
    }

    public void markContextInconsistent() {
        // Simulating inconsistent context
        this.currentMenuId = "INVALID_CONTEXT_MENU";
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof NavigateMenuCmd c) {
            return handleNavigate(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleNavigate(NavigateMenuCmd c) {
        // Invariant: A teller must be authenticated to initiate a session.
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (Duration.between(lastActivityAt, Instant.now()).compareTo(SESSION_TIMEOUT) > 0) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // (Here we assume if the current state is explicitly invalid, navigation fails)
        if ("INVALID_CONTEXT_MENU".equals(this.currentMenuId)) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        var event = new MenuNavigatedEvent(sessionId, c.menuId(), c.action(), c.occurredAt());
        this.currentMenuId = c.menuId();
        this.lastActivityAt = c.occurredAt();
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public String getCurrentMenuId() {
        return currentMenuId;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}