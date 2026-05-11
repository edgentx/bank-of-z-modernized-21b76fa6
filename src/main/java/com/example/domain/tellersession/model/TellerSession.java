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
    private boolean authenticated;
    private String currentMenuId;
    private String currentContext;
    private Instant lastActivityAt;
    private boolean active;

    // Configured timeout period in minutes (e.g., 15 minutes)
    private static final long TIMEOUT_MINUTES = 15;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.active = true;
        this.lastActivityAt = Instant.now();
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof NavigateMenuCmd) {
            return handleNavigateMenu((NavigateMenuCmd) cmd);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleNavigateMenu(NavigateMenuCmd cmd) {
        // Invariant: Authenticated
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Timeout
        if (hasTimedOut()) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation State Accuracy
        // Validate that the command's current context matches the aggregate's state
        if (currentMenuId != null && !currentMenuId.equals(cmd.currentMenuId())) {
             throw new IllegalStateException("Navigation state must accurately reflect the current operational context (Menu ID mismatch).");
        }
        // Note: context validation could be added here if more specific logic is required

        // Apply state changes
        this.currentMenuId = cmd.targetMenuId();
        this.currentContext = cmd.targetContext(); // Assuming target context is passed or derived
        this.lastActivityAt = Instant.now();

        var event = new MenuNavigatedEvent(
            this.sessionId,
            cmd.targetMenuId(),
            cmd.action(),
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean hasTimedOut() {
        if (lastActivityAt == null) return true;
        return lastActivityAt.plus(TIMEOUT_MINUTES, ChronoUnit.MINUTES).isBefore(Instant.now());
    }

    // Getters for testing / state hydration
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
    public void setCurrentMenuId(String menuId) { this.currentMenuId = menuId; }
    public void setCurrentContext(String context) { this.currentContext = context; }
    public void setLastActivityAt(Instant time) { this.lastActivityAt = time; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isAuthenticated() { return authenticated; }
    public String getCurrentMenuId() { return currentMenuId; }
    public Instant getLastActivityAt() { return lastActivityAt; }
}
