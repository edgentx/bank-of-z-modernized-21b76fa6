package com.example.domain.telllersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * TellerSession Aggregate (S-19, S-20)
 * Handles UI navigation state and session invariants for the 3270 emulator.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String currentMenuId;
    private boolean authenticated;
    private Instant sessionTimeout;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        // Handle initialization first (part of session lifecycle)
        if (cmd instanceof InitiateTellerSessionCmd c) {
            return initiate(c);
        }

        // Check invariant: Session must be initiated/active
        if (!authenticated) {
            throw new IllegalStateException("Session not initiated or authenticated.");
        }

        // Handle navigation
        if (cmd instanceof NavigateMenuCmd c) {
            return navigate(c);
        }

        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> initiate(InitiateTellerSessionCmd c) {
        if (c.tellerId() == null || c.tellerId().isBlank()) throw new IllegalArgumentException("tellerId required");
        if (c.sessionTimeout() == null) throw new IllegalArgumentException("sessionTimeout required");

        this.tellerId = c.tellerId();
        this.currentMenuId = c.initialMenu();
        this.authenticated = true;
        this.sessionTimeout = c.sessionTimeout();

        // In a real scenario, we'd emit a SessionInitiatedEvent here. 
        // For this story, we focus on NavigateMenuCmd, but need state setup.
        // We return empty list or an internal event to satisfy the Execute pattern.
        // To align with strict "emit event" rules, we can emit an internal event or just return.
        // Given BDD implies explicit event emission for navigation, we treat state setup as direct.
        // Or better: emit a lifecycle event.
        return List.of(); // Or new SessionInitiatedEvent(...)
    }

    private List<DomainEvent> navigate(NavigateMenuCmd c) {
        // Invariant: Timeout check
        if (Instant.now().isAfter(sessionTimeout)) {
            throw new IllegalStateException("Session timed out at " + sessionTimeout);
        }

        // Invariant: Contextual Navigation (Simplified for demo: Logic checks valid transition)
        if ("ADMIN_SUPER_USER".equals(c.menuId()) && !"SECURITY".equals(currentMenuId)) {
            throw new IllegalArgumentException("Invalid navigation context: Cannot access Admin menu from " + currentMenuId);
        }

        // Apply state change
        this.currentMenuId = c.menuId();
        // Refresh timeout on activity?
        // this.sessionTimeout = Instant.now().plusSeconds(3600); // Optional: sliding expiration

        var event = new MenuNavigatedEvent(sessionId, c.menuId(), c.action(), Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}