package com.example.domain.navigation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Teller Session Aggregate.
 * Manages the lifecycle of a teller's terminal session, authentication state, and context.
 * Story: S-20
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private boolean isAuthenticated;
    private boolean isActive;
    private Instant lastActivityAt;
    private Instant sessionStartedAt;
    private Duration timeoutDuration;

    // Invariants
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(30);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.timeoutDuration = DEFAULT_TIMEOUT;
        this.isActive = false;
    }

    @Override
    public String id() {
        return sessionId;
    }

    /**
     * Internal helper to verify authentication.
     */
    private void ensureAuthenticated() {
        if (!isAuthenticated) {
            throw new IllegalStateException("Teller must be authenticated.");
        }
    }

    /**
     * Internal helper to verify activity/timeout.
     */
    private void ensureActive() {
        if (!isActive) {
            throw new IllegalStateException("Session is not active.");
        }
        if (lastActivityAt == null) {
            throw new IllegalStateException("Session activity state is invalid.");
        }
        Instant now = Instant.now();
        long inactiveMinutes = ChronoUnit.MINUTES.between(lastActivityAt, now);
        if (inactiveMinutes > timeoutDuration.toMinutes()) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return endSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // Invariant Check: Authentication
        ensureAuthenticated();

        // Invariant Check: Timeout / Activity
        ensureActive();

        // Invariant Check: Navigation Context
        // Implicitly satisfied by being in an active state to reach this point in this simplified aggregate model.
        // A more complex model might check for open transactions or locked records.

        var event = new SessionEndedEvent(cmd.sessionId(), cmd.occurredAt());

        // Apply state changes
        this.isActive = false;
        this.tellerId = null; // Clear sensitive state
        this.isAuthenticated = false;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // --- Public Test Probes / Setters ---

    public void markAuthenticated(String tellerId) {
        this.tellerId = tellerId;
        this.isAuthenticated = true;
        this.isActive = true;
        this.sessionStartedAt = Instant.now();
        this.lastActivityAt = Instant.now();
    }

    public void markTimedOut() {
        this.lastActivityAt = Instant.now().minus(Duration.ofHours(1));
    }

    public void markUnauthenticated() {
        this.isAuthenticated = false;
    }

    public void markInactive() {
        this.isActive = false;
    }

    public String getTellerId() {
        return tellerId;
    }

    public boolean isActive() {
        return isActive;
    }
}