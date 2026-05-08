package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String currentMenuId;
    private boolean authenticated;
    private boolean active;
    private boolean timedOut;
    private boolean validNavigationState;

    public TellerSession(String sessionId, String tellerId) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.authenticated = false;
        this.active = false;
        this.timedOut = false;
        this.validNavigationState = true; // Assume valid initially
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
        if (!authenticated) {
            throw new IllegalStateException("Teller must be authenticated to perform navigation.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (timedOut) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        if (!validNavigationState) {
            throw new IllegalStateException("Navigation state is invalid for the current operational context.");
        }

        // Validate inputs
        if (cmd.menuId() == null || cmd.menuId().isBlank()) {
            throw new IllegalArgumentException("menuId is required");
        }

        var event = new MenuNavigatedEvent(sessionId, cmd.menuId(), cmd.action(), Instant.now());
        this.currentMenuId = cmd.menuId();
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Domain helper methods for testing state manipulation
    public void authenticate(String tellerId) {
        this.authenticated = true;
    }

    public void activate() {
        this.active = true;
    }

    public void simulateTimeout() {
        this.timedOut = true;
    }

    public void markNavigationInvalid() {
        this.validNavigationState = false;
    }

    public String getCurrentMenuId() {
        return currentMenuId;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
