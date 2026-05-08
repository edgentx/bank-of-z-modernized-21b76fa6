package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public class TellerSession extends AggregateRoot {
    private final String sessionId;
    private String tellerId;
    private boolean authenticated = false;
    private Instant lastActivity;
    private static final Duration TIMEOUT = Duration.ofMinutes(30);
    // Simulated authorized menus for a standard teller
    private static final Set<String> AUTHORIZED_MENUS = Set.of(
        "ACCOUNT_SUMMARY", "MAIN_MENU", "CUSTOMER_SEARCH", "POST_TX"
    );

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        // By default, a new session is not active and not authenticated
        this.lastActivity = Instant.now();
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
        // Invariant 1: Authentication check
        if (!authenticated) {
            throw new IllegalStateException("Teller must be authenticated to initiate a session.");
        }

        // Invariant 2: Timeout check
        if (Duration.between(lastActivity, Instant.now()).compareTo(TIMEOUT) > 0) {
             throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant 3: Operational Context check
        if (!AUTHORIZED_MENUS.contains(cmd.menuId())) {
             throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context. Unauthorized menu: " + cmd.menuId());
        }

        // Success path: Emit event and update state
        MenuNavigatedEvent event = new MenuNavigatedEvent(sessionId, cmd.menuId(), cmd.action(), Instant.now());
        this.lastActivity = event.occurredAt();
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Test helpers (simulating event sourcing rehydration or setup)
    public void markAsAuthenticated() {
        this.authenticated = true;
        this.lastActivity = Instant.now();
    }

    public void markAsActive() {
        this.lastActivity = Instant.now();
    }

    public void forceLastActivity(Instant time) {
        this.lastActivity = time;
    }
}
