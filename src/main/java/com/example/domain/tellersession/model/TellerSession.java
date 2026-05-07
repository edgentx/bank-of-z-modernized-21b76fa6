package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * TellerSession Aggregate.
 * Handles the lifecycle of a teller's UI session, enforcing invariants about
 * authentication, timeouts, and navigation state.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private TellerSessionState state;
    private Instant lastActivityAt;
    private final Duration timeoutDuration = Duration.ofMinutes(30);
    private boolean isNavigationStateValid = true;

    // Package-private constructor for test harnessing/stubbing
    TellerSession(String sessionId, String tellerId) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.state = TellerSessionState.AUTHENTICATED;
        this.lastActivityAt = Instant.now();
    }

    // Public constructor for reconstruction (e.g., from repository)
    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.state = TellerSessionState.AUTHENTICATED; // Default for new aggregate
        this.lastActivityAt = Instant.now();
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return handleEndSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleEndSession(EndSessionCmd cmd) {
        // Invariant Check 1: Teller must be authenticated
        if (state != TellerSessionState.AUTHENTICATED) {
            throw new IllegalStateException(
                String.format("Cannot end session %s: Session is in state %s. Authenticated state required.",
                    sessionId, state)
            );
        }

        // Invariant Check 2: Session must not have timed out
        if (hasTimedOut()) {
            this.state = TellerSessionState.TIMED_OUT;
            throw new IllegalStateException(
                String.format("Cannot end session %s: Session has timed out due to inactivity.", sessionId)
            );
        }

        // Invariant Check 3: Navigation state must be valid
        if (!isNavigationStateValid) {
            throw new IllegalStateException(
                String.format("Cannot end session %s: Navigation state is corrupted or does not reflect operational context.", sessionId)
            );
        }

        // Apply logic
        var event = new SessionEndedEvent(this.sessionId, this.tellerId, Instant.now());
        this.state = TellerSessionState.UNAUTHENTICATED; // Transition state effectively ending it
        this.tellerId = null; // Clear sensitive state
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean hasTimedOut() {
        if (lastActivityAt == null) return true;
        Instant now = Instant.now();
        long elapsed = lastActivityAt.until(now, ChronoUnit.MINUTES);
        return elapsed > timeoutDuration.toMinutes();
    }

    // --- Test Utilities ---
    // These methods are used to simulate invalid states for BDD scenario validation.

    void markUnauthenticated() {
        this.state = TellerSessionState.UNAUTHENTICATED;
    }

    void forceTimeoutState() {
        // Simulate timeout by setting last activity far in the past
        this.lastActivityAt = Instant.now().minus(timeoutDuration).minusSeconds(60);
    }

    void corruptNavigationState() {
        this.isNavigationStateValid = false;
    }

    public TellerSessionState getState() {
        return state;
    }

    public String getTellerId() {
        return tellerId;
    }
}
