package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * TellerSession aggregate handling session lifecycle and UI navigation.
 * Enforces authentication, timeout, and navigation state invariants.
 * S-19: Implement NavigateMenuCmd.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private boolean authenticated;
    private Instant lastActivityAt;
    private String currentMenuId;
    private String currentContext; // e.g., "CUSTOMER_VIEW", "ACCOUNT_VIEW"
    private final long timeoutMinutes = 30; // Configured period of inactivity

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
    }

    // Initializer hook for tests to set up a valid state without events
    public void markAuthenticated(String tellerId) {
        this.tellerId = tellerId;
        this.authenticated = true;
        this.lastActivityAt = Instant.now();
    }

    // Initializer hook for tests to set current menu context
    public void setContext(String menuId, String context) {
        this.currentMenuId = menuId;
        this.currentContext = context;
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
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (lastActivityAt != null) {
            long minutesSinceActive = ChronoUnit.MINUTES.between(lastActivityAt, Instant.now());
            if (minutesSinceActive > timeoutMinutes) {
                throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
            }
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // Here we enforce that the command claims to come from the current known state.
        if (currentMenuId != null && !currentMenuId.equals(cmd.currentMenuId())) {
             throw new IllegalStateException("Navigation state must accurately reflect the current operational context (Menu mismatch).");
        }
        if (currentContext != null && !currentContext.equals(cmd.currentContext())) {
             throw new IllegalStateException("Navigation state must accurately reflect the current operational context (Context mismatch).");
        }

        String targetMenuId = cmd.targetMenuId();
        if (targetMenuId == null || targetMenuId.isBlank()) {
            throw new IllegalArgumentException("targetMenuId required");
        }

        // Update state
        this.lastActivityAt = Instant.now();
        this.currentMenuId = targetMenuId;
        // Context may or may not change based on menu, for now we assume context carries over or updates via specific logic not defined here
        // this.currentContext = ... 

        var event = new MenuNavigatedEvent(sessionId, cmd.currentMenuId(), targetMenuId, Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Getters for testing
    public boolean isAuthenticated() { return authenticated; }
    public Instant getLastActivityAt() { return lastActivityAt; }
    public String getCurrentMenuId() { return currentMenuId; }
}