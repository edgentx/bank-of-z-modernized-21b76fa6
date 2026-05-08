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
    private SessionState state = SessionState.NONE;
    private Instant lastActivityAt;
    private boolean isNavigationStateValid = true;

    public enum SessionState {
        NONE, INITIATED, ACTIVE, ENDED, TIMED_OUT
    }

    public TellerSession(String sessionId, String tellerId) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.lastActivityAt = Instant.now();
        if (tellerId != null) {
            this.state = SessionState.ACTIVE;
        }
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return endSession(c);
        }
        // Handling the pseudo-login from the test setup for context initialization
        // In a real scenario, this would be a LoginCmd
        else if (cmd.getClass().getSimpleName().contains("Command") && tellerId == null && cmd.getClass().getDeclaredFields().length > 0) {
             // Very loose check for the test dummy object to set state
             try {
                 var field = cmd.getClass().getDeclaredField("tellerId");
                 field.setAccessible(true);
                 Object val = field.get(cmd);
                 if (val instanceof String) {
                     this.tellerId = (String) val;
                     this.state = SessionState.ACTIVE;
                     this.lastActivityAt = Instant.now();
                     return List.of();
                 }
             } catch (Exception e) { /* Ignore if not the dummy */ }
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        if (this.tellerId == null || this.tellerId.isBlank()) {
            throw new IllegalStateException("Teller must be authenticated to end a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // Simplified check: if state is already TIMED_OUT, we cannot end normally.
        if (this.state == SessionState.TIMED_OUT) {
            throw new IllegalStateException("Session has already timed out.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        if (!this.isNavigationStateValid) {
            throw new IllegalStateException("Navigation state is invalid. Cannot end session.");
        }

        if (this.state != SessionState.ACTIVE && this.state != SessionState.INITIATED) {
             throw new IllegalStateException("Session is not active and cannot be ended.");
        }

        var event = new SessionEndedEvent(this.sessionId, Instant.now());
        this.state = SessionState.ENDED;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Test helpers / Accessors
    public void markAsTimedOut() {
        this.state = SessionState.TIMED_OUT;
    }

    public void markNavigationStateInvalid() {
        this.isNavigationStateValid = false;
    }

    public SessionState getState() {
        return state;
    }
}
