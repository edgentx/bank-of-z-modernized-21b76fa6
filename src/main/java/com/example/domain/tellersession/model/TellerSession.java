package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * TellerSession Aggregate Root.
 * Manages the state of a teller's terminal session, including authentication status, current screen context,
 * and session timeouts (legacy 3270 muscle memory preservation).
 *
 * S-19: NavigateMenuCmd Implementation
 */
public class TellerSession extends AggregateRoot {

    // Configuration for session timeout (e.g., 15 minutes)
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    private final String sessionId;
    private boolean authenticated;
    private String currentMenuId;
    private Instant lastActivityAt;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof NavigateMenuCmd c) {
            return navigateMenu(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> navigateMenu(NavigateMenuCmd c) {
        // Invariant: Authentication
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Session Timeout
        if (lastActivityAt != null && Duration.between(lastActivityAt, Instant.now()).compareTo(SESSION_TIMEOUT) > 0) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation State Integrity
        // We assume valid navigation if input is non-null, effectively updating the context.
        if (c.menuId() == null || c.menuId().isBlank()) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context (menuId required).");
        }

        var event = new MenuNavigatedEvent(c.sessionId(), c.menuId(), c.action(), c.occurredAt());

        // Apply state changes
        this.currentMenuId = c.menuId();
        this.lastActivityAt = c.occurredAt();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // --- Test / Infrastructure Helpers ---

    public void markAuthenticated() {
        this.authenticated = true;
    }

    public void setLastActivityAt(Instant timestamp) {
        this.lastActivityAt = timestamp;
    }

    public void setAuthentication(boolean status) {
        this.authenticated = status;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public String getCurrentMenuId() {
        return currentMenuId;
    }
}
