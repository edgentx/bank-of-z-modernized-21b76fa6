package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.shared.AggregateRoot;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Aggregate representing a Bank Teller's UI session.
 * Manages authentication state, navigation context, and timeouts.
 */
public class TellerSessionAggregate extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String navigationContext;
    private Instant lastActivityAt;
    private boolean isActive;

    // Configuration: Session timeout duration
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);

    public TellerSessionAggregate(String sessionId) {
        this.sessionId = sessionId;
        // Initialize in a blank/unauthenticated state
        this.lastActivityAt = Instant.now();
        this.isActive = false;
    }

    @Override
    public String id() {
        return sessionId;
    }

    /**
     * Test helper to simulate state hydration.
     * In a real application, state is built by applying events from the EventStore.
     */
    public void hydrate(String tellerId, String navigationContext, Instant lastActivityAt) {
        this.tellerId = tellerId;
        this.navigationContext = navigationContext;
        this.lastActivityAt = lastActivityAt;
        if (tellerId != null && navigationContext != null) {
            this.isActive = true;
        }
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd) {
            return handleEndSession((EndSessionCmd) cmd);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleEndSession(EndSessionCmd cmd) {
        // Invariant: A teller must be authenticated
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalStateException("Cannot end session: No authenticated teller found.");
        }

        // Invariant: Navigation state must be valid
        if (navigationContext == null || navigationContext.isBlank()) {
            throw new IllegalStateException("Cannot end session: Navigation context is corrupted or invalid.");
        }

        // Invariant: Session must not be timed out (Business rule: usually you can't end a dead session, or you must log it)
        // Here we enforce that we cannot perform operations on a timed-out session.
        if (isTimedOut()) {
            throw new IllegalStateException("Cannot end session: Session has timed out due to inactivity.");
        }

        SessionEndedEvent event = new SessionEndedEvent(sessionId, Instant.now());
        
        // Apply state changes
        this.isActive = false;
        this.tellerId = null; // Clear sensitive state
        this.navigationContext = null; // Clear navigation state
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean isTimedOut() {
        if (lastActivityAt == null) return true;
        return Instant.now().isAfter(lastActivityAt.plus(SESSION_TIMEOUT));
    }
}