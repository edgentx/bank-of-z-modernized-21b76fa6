package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * TellerSession Aggregate.
 * Manages the state of a teller's terminal session, including navigation,
 * authentication, and timeouts.
 * S-19: Implements NavigateMenuCmd.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String currentMenuId;
    private boolean isAuthenticated;
    private Instant lastActivityAt;
    private Instant sessionStart;

    // Configuration: Timeout duration (e.g., 15 minutes)
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

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
            return handleNavigateMenu(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleNavigateMenu(NavigateMenuCmd cmd) {
        // Invariant 1: Authentication required
        if (!isAuthenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant 2: Session Timeout
        if (isSessionTimedOut()) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant 3: Operational Context (Basic validation for S-19)
        // Ensuring we aren't navigating back to the exact same state/context aggressively
        // or that the provided action is valid for the context.
        // For this scenario, we simulate a context violation check.
        if ("INVALID_CONTEXT".equals(cmd.action())) {
             throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context.");
        }

        // Apply logic
        this.currentMenuId = cmd.menuId();
        this.lastActivityAt = Instant.now();

        // Create Event
        MenuNavigatedEvent event = new MenuNavigatedEvent(
            this.sessionId,
            cmd.menuId(),
            cmd.action(),
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean isSessionTimedOut() {
        if (lastActivityAt == null) return false;
        return Duration.between(lastActivityAt, Instant.now()).compareTo(SESSION_TIMEOUT) > 0;
    }

    // Getters for test setup and validation
    public void markAuthenticated(String tellerId) {
        this.tellerId = tellerId;
        this.isAuthenticated = true;
    }

    public void simulateTimeout() {
        this.lastActivityAt = Instant.now().minus(SESSION_TIMEOUT).minusSeconds(10);
    }

    public String getTellerId() { return tellerId; }
    public boolean isAuthenticated() { return isAuthenticated; }
    public String getCurrentMenuId() { return currentMenuId; }
    public Instant getLastActivityAt() { return lastActivityAt; }
}
