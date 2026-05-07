package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * TellerSession aggregate.
 * Manages teller terminal lifecycle, authentication state, and context locks.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private boolean authenticated = false;
    private Instant lastActivityAt;
    private boolean transactionLocked = false;

    // Configuration: 30 minutes timeout
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd) {
            return endSession((EndSessionCmd) cmd);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // Invariant: Authentication check
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Timeout check
        if (lastActivityAt != null && lastActivityAt.plus(SESSION_TIMEOUT).isBefore(Instant.now())) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation/Context check
        if (transactionLocked) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context (Transaction Locked).");
        }

        // Apply state changes
        SessionEndedEvent event = new SessionEndedEvent(this.sessionId, Instant.now());
        
        // Update internal state (termination)
        this.authenticated = false;
        this.tellerId = null;
        this.lastActivityAt = null;
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    /**
     * Helper to rebuild state from events (simplified for this scenario).
     * In a full implementation, this would be the 'apply' method overload
     * used by the repository to hydrate the aggregate.
     */
    public void apply(SessionStartedEvent event) {
        this.tellerId = event.tellerId();
        this.authenticated = true;
        this.lastActivityAt = event.occurredAt();
        incrementVersion();
    }

    // Test helpers to simulate specific state violations
    public void setTransactionLocked(boolean locked) {
        this.transactionLocked = locked;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }
}
