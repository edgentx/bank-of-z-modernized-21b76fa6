package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.time.Duration;
import java.util.List;

/**
 * TellerSession aggregate.
 * S-19: Implement NavigateMenuCmd.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean authenticated;
    private Instant lastActivityAt;
    private String currentMenuId;
    private boolean active;

    // Configuration for session timeout
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
        this.active = true;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof NavigateMenuCmd c) {
            return handleNavigateMenu(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleNavigateMenu(NavigateMenuCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (Instant.now().isAfter(lastActivityAt.plus(SESSION_TIMEOUT))) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // For this story, we assume valid navigation means strictly moving to a new context.
        if (cmd.menuId().equals(this.currentMenuId)) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context (duplicate navigation).");
        }

        // Apply state change
        String previousMenuId = this.currentMenuId;
        this.currentMenuId = cmd.menuId();
        this.lastActivityAt = Instant.now();

        MenuNavigatedEvent event = new MenuNavigatedEvent(
            sessionId,
            previousMenuId,
            cmd.menuId(),
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Getters for testing and state hydration
    public boolean isAuthenticated() {
        return authenticated;
    }

    public void markAuthenticated() {
        this.authenticated = true;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    // Used to simulate timeout in testing
    public void setLastActivityAt(Instant time) {
        this.lastActivityAt = time;
    }

    public String getCurrentMenuId() {
        return currentMenuId;
    }

    public void setCurrentMenuId(String menuId) {
        this.currentMenuId = menuId;
    }
}
