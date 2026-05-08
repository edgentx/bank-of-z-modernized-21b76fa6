package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Teller Session Aggregate.
 * Manages the lifecycle of a bank teller's UI session, including authentication,
 * activity tracking, and navigation state validity.
 * 
 * S-20: Implement EndSessionCmd on TellerSession
 */
public class TellerSession extends AggregateRoot {

    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    private final String sessionId;
    private boolean authenticated;
    private Instant lastActivityAt;
    private boolean contextValid;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.authenticated = false;
        this.lastActivityAt = Instant.now();
        this.contextValid = true;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd) {
            return endSession();
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession() {
        // Invariant: A teller must be authenticated to initiate a session.
        if (!authenticated) {
            throw new IllegalStateException("Cannot end session: Teller is not authenticated.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        if (isSessionTimedOut()) {
            throw new IllegalStateException("Cannot end session: Session has already timed out due to inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        if (!contextValid) {
            throw new IllegalStateException("Cannot end session: Navigation state is invalid or corrupted.");
        }

        SessionEndedEvent event = new SessionEndedEvent(this.sessionId);
        addEvent(event);
        incrementVersion();
        
        // Clear sensitive state
        this.authenticated = false;
        
        return List.of(event);
    }

    private boolean isSessionTimedOut() {
        return lastActivityAt != null && 
               Instant.now().isAfter(lastActivityAt.plus(SESSION_TIMEOUT));
    }

    // State setters for testing and reconstruction
    public void markAuthenticated() {
        this.authenticated = true;
        this.lastActivityAt = Instant.now();
    }

    public void setLastActivityAt(Instant time) {
        this.lastActivityAt = time;
    }

    public void setContextValid(boolean valid) {
        this.contextValid = valid;
    }

    // Getters for assertions
    public boolean isAuthenticated() {
        return authenticated;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public boolean isContextValid() {
        return contextValid;
    }
}
