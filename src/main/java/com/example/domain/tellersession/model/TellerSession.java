package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {
    private final String sessionId;
    private String tellerId;
    private boolean authenticated = false;
    private Instant lastActivityAt;
    private String currentMenuId = "ROOT"; // Default context
    
    // Configuration
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof NavigateMenuCmd c) return navigate(c);
        if (cmd instanceof LoginTellerCmd c) return login(c);
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> login(LoginTellerCmd c) {
        if (authenticated) throw new IllegalStateException("Already logged in");
        
        this.tellerId = c.tellerId();
        this.authenticated = true;
        this.lastActivityAt = c.occurredAt();
        this.currentMenuId = "MAIN_MENU";
        
        var event = new TellerLoggedInEvent(sessionId, tellerId, c.occurredAt());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private List<DomainEvent> navigate(NavigateMenuCmd c) {
        // Invariant 1: Authentication Check
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant 2: Timeout Check
        if (lastActivityAt != null && Duration.between(lastActivityAt, c.occurredAt()).compareTo(SESSION_TIMEOUT) > 0) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant 3: Navigation State Context Check
        // Example rule: Cannot access specific high-risk screens directly from ROOT
        if ("POST_TX_MENU".equals(c.menuId()) && "MAIN_MENU".equals(currentMenuId)) {
             throw new IllegalStateException("Navigation state must accurately reflect the current operational context: Invalid transition.");
        }

        // Update State
        this.currentMenuId = c.menuId();
        this.lastActivityAt = c.occurredAt();

        var event = new MenuNavigatedEvent(sessionId, c.menuId(), c.action(), c.occurredAt());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}
