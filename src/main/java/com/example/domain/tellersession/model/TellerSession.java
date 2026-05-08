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
    private boolean locked = false;
    private Duration timeoutDuration = Duration.ofMinutes(30);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now(); // Default to now on construction
    }

    @Override
    public String id() {
        return sessionId;
    }

    /**
     * Helper to set up the aggregate for testing.
     * In a real scenario, this state comes from event sourcing.
     */
    public void initialize(String tellerId) {
        this.tellerId = tellerId;
        this.authenticated = true;
        this.lastActivityAt = Instant.now();
    }

    /**
     * Helper for testing timeout scenarios.
     */
    public void setLastActivityAt(Instant time) {
        this.lastActivityAt = time;
    }

    /**
     * Helper for testing invalid context scenarios.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
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
            throw new IllegalStateException("Teller must be authenticated.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (lastActivityAt != null && Duration.between(lastActivityAt, Instant.now()).compareTo(timeoutDuration) > 0) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        if (locked) {
            throw new IllegalStateException("Cannot navigate: session is locked due to operational state.");
        }

        if (cmd.menuId() == null || cmd.menuId().isBlank()) {
            throw new IllegalArgumentException("menuId cannot be blank");
        }

        if (cmd.action() == null || cmd.action().isBlank()) {
            throw new IllegalArgumentException("action cannot be blank");
        }

        var event = new MenuNavigatedEvent(sessionId, cmd.menuId(), cmd.action(), Instant.now());
        addEvent(event);
        incrementVersion();
        // Update state side-effect
        this.lastActivityAt = event.occurredAt();

        return List.of(event);
    }
}