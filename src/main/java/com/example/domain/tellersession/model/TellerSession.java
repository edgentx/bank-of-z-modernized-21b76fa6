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
 * Manages the state of a bank teller's terminal session, including authentication, timeouts, and UI navigation context.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private boolean authenticated;
    private boolean active;
    private Instant lastActivityAt;
    private Duration timeoutDuration;
    private String navigationContext; // e.g., "MAIN_MENU", "CASH_DEPOSIT"

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        // Default invariants for a fresh session
        this.authenticated = false;
        this.active = true;
        this.lastActivityAt = Instant.now();
        this.timeoutDuration = Duration.ofMinutes(15); // Standard timeout
        this.navigationContext = "INITIAL";
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
        // NOTE: StartSessionCmd or other commands would be handled here in future stories
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // Invariant 1: Sessions must timeout after a configured period of inactivity.
        if (hasTimedOut()) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // Invariant 2: A teller must be authenticated to initiate a session.
        // NOTE: This invariant phrasing in the AC implies checking if they are authenticated. 
        // However, ending a session is often allowed even if unauthenticated (e.g. canceling login). 
        // But strictly following the AC: "EndSessionCmd rejected — A teller must be authenticated..."
        // Contextually, this usually applies to actions *within* a session, but we enforce it here if required.
        // We will interpret this as: The session must be in a valid state to terminate cleanly, or 
        // specifically that the invariant of "Authenticity" holds. 
        // Given the specific AC, we will throw if !authenticated.
        if (!authenticated) {
             throw new IllegalStateException("Teller must be authenticated to end session formally.");
        }

        // Invariant 3: Navigation state must accurately reflect the current operational context.
        // We check if the context is in a valid state to end (e.g., not mid-transaction).
        if (navigationContext == null || navigationContext.equals("UNKNOWN")) {
            throw new IllegalStateException("Navigation state is invalid.");
        }

        var event = new SessionEndedEvent(this.sessionId, this.tellerId, Instant.now());
        this.active = false;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean hasTimedOut() {
        return lastActivityAt.plus(timeoutDuration).isBefore(Instant.now());
    }

    // Test helpers / State accessors
    public void markAuthenticated(String tellerId) {
        this.tellerId = tellerId;
        this.authenticated = true;
    }
    
    public void setLastActivityAt(Instant time) {
        this.lastActivityAt = time;
    }

    public void setTimeoutDuration(Duration duration) {
        this.timeoutDuration = duration;
    }

    public void setNavigationContext(String context) {
        this.navigationContext = context;
    }
    
    public boolean isActive() {
        return active;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
