package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {
    private final String sessionId;
    private String tellerId;
    private boolean authenticated;
    private boolean active;
    private Instant lastActivityAt;
    private String currentMenuId;
    private String operationalContext; // Represents screen context

    // Invariants: 15 minutes timeout
    private static final long SESSION_TIMEOUT_MINUTES = 15;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.active = false; // Session starts inactive
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
        // Future commands (InitiateSession, EndSession, etc.) would go here
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleNavigateMenu(NavigateMenuCmd cmd) {
        // 1. Invariant: A teller must be authenticated to initiate a session (and navigate).
        if (!authenticated) {
            throw new IllegalStateException("Teller must be authenticated to navigate.");
        }

        // 2. Invariant: Sessions must timeout after a configured period of inactivity.
        if (isSessionExpired()) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // 3. Invariant: Navigation state must accurately reflect the current operational context.
        // (Verify current state matches expectation, or simply ensure valid navigation)
        if (this.currentMenuId != null && !this.currentMenuId.equals(cmd.currentMenuId())) {
            throw new IllegalStateException("Navigation state mismatch: expected menu " + cmd.currentMenuId() + " but currently at " + this.currentMenuId);
        }
        
        if (cmd.targetMenuId() == null || cmd.targetMenuId().isBlank()) {
             throw new IllegalArgumentException("targetMenuId is required");
        }

        // Apply state change
        String previousMenu = this.currentMenuId;
        this.currentMenuId = cmd.targetMenuId();
        if (cmd.targetContext() != null) {
            this.operationalContext = cmd.targetContext();
        }
        this.lastActivityAt = Instant.now();

        MenuNavigatedEvent event = new MenuNavigatedEvent(
            this.sessionId,
            previousMenu,
            this.currentMenuId,
            this.operationalContext,
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean isSessionExpired() {
        if (lastActivityAt == null) return true;
        return lastActivityAt.plusSeconds(SESSION_TIMEOUT_MINUTES * 60).isBefore(Instant.now());
    }

    // Getters for testing and state reconstruction
    public String getTellerId() { return tellerId; }
    public boolean isAuthenticated() { return authenticated; }
    public String getCurrentMenuId() { return currentMenuId; }
    public String getOperationalContext() { return operationalContext; }
}