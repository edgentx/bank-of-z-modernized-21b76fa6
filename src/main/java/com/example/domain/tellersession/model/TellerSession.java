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
    private String tellerId;
    private String currentMenuId;
    private String currentAction;
    private Instant lastActivityAt;
    private boolean isAuthenticated;
    private boolean isActive;

    // Configurable timeout in minutes (mockable value)
    private static final long SESSION_TIMEOUT_MINUTES = 30;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
        this.isActive = true;
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
        if (!isAuthenticated) {
            throw new IllegalStateException("Teller must be authenticated.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (lastActivityAt != null) {
            long minutesSinceActivity = ChronoUnit.MINUTES.between(lastActivityAt, Instant.now());
            if (minutesSinceActivity > SESSION_TIMEOUT_MINUTES) {
                throw new IllegalStateException("Session has timed out due to inactivity.");
            }
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // (Assuming the provided context must match the current session state for the transition to be valid)
        if (cmd.currentContext() != null && !cmd.currentContext().equals(this.currentMenuId)) {
             throw new IllegalArgumentException("Navigation state conflict: Current context does not match operational state.");
        }

        String previousMenu = this.currentMenuId;
        this.currentMenuId = cmd.targetMenuId();
        this.currentAction = cmd.action();
        this.lastActivityAt = Instant.now();

        // Create Event: sessionId, previousMenu, newMenu, action, timestamp
        MenuNavigatedEvent event = new MenuNavigatedEvent(
            this.sessionId,
            previousMenu,
            this.currentMenuId,
            this.currentAction,
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Test hooks
    public void setAuthenticated(boolean authenticated) {
        this.isAuthenticated = authenticated;
    }

    public void setLastActivityAt(Instant time) {
        this.lastActivityAt = time;
    }

    public void setCurrentMenuId(String menuId) {
        this.currentMenuId = menuId;
    }
}