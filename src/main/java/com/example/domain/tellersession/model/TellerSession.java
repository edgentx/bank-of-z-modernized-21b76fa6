package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class TellerSession extends AggregateRoot {
    private final UUID sessionId;
    private String tellerId;
    private Instant lastActivityAt;
    private boolean active;
    private boolean authenticated;
    private boolean operationalContextValid;

    public TellerSession(UUID sessionId) {
        this.sessionId = sessionId;
        this.active = false;
        this.authenticated = false;
        this.operationalContextValid = true;
        this.lastActivityAt = Instant.now();
    }

    // Public factory for testing: initialize a valid session
    public void initializeValidSession(String tellerId) {
        this.tellerId = tellerId;
        this.active = true;
        this.authenticated = true;
        this.operationalContextValid = true;
        this.lastActivityAt = Instant.now();
    }

    @Override
    public String id() {
        return sessionId.toString();
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd) {
            return handleEndSession((EndSessionCmd) cmd);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleEndSession(EndSessionCmd cmd) {
        // Invariant Check: A teller must be authenticated to initiate a session
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant Check: Navigation state must accurately reflect the current operational context
        if (!operationalContextValid) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // Invariant Check: Sessions must timeout after a configured period of inactivity
        // Assuming a hardcoded timeout of 15 minutes for this domain story
        Instant timeoutThreshold = Instant.now().minusSeconds(900);
        if (lastActivityAt.isBefore(timeoutThreshold)) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        if (!active) {
            throw new IllegalStateException("Session is not active.");
        }

        // Apply state changes
        this.active = false;
        this.tellerId = null; // Clear sensitive state
        
        // Create event
        SessionEndedEvent event = new SessionEndedEvent("SessionEnded", sessionId, Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Test setters for specific violation scenarios
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
    public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    public void setOperationalContextValid(boolean operationalContextValid) { this.operationalContextValid = operationalContextValid; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isActive() { return active; }
}
