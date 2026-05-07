package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean isAuthenticated = false;
    private Instant lastActivityAt;
    private boolean isActive = false;
    private String navigationContext = "HOME";

    // Configured timeout (e.g., 15 minutes)
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
        // Default state: not authenticated, not active.
        // In a real scenario, we would hydrate from events. 
        // For BDD tests to pass 'Given valid', we assume the test setup 
        // implies the state required or we handle state transitions here.
    }

    @Override
    public String id() {
        return sessionId;
    }

    // Helper to simulate valid state for the "Happy Path" test scenario
    // (In a real app, this would be handled by applying previous events)
    public void markAuthenticated() {
        this.isAuthenticated = true;
        this.isActive = true;
        this.lastActivityAt = Instant.now();
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return endSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // Invariant 1: Must be authenticated
        if (!isAuthenticated) {
            throw new IllegalStateException("Teller must be authenticated to end a session.");
        }

        // Invariant 2: Timeout check
        if (lastActivityAt != null) {
            Duration inactive = Duration.between(lastActivityAt, Instant.now());
            if (inactive.compareTo(SESSION_TIMEOUT) > 0) {
                throw new IllegalStateException("Session has timed out due to inactivity.");
            }
        }

        // Invariant 3: Navigation state accuracy
        if (navigationContext == null || navigationContext.isBlank()) {
             // This simulates a state corruption or invalid context
            throw new IllegalStateException("Navigation state is invalid or corrupted.");
        }

        // Success
        SessionEndedEvent event = new SessionEndedEvent(sessionId, Instant.now());
        addEvent(event);
        incrementVersion();
        
        // Update internal state
        this.isActive = false;
        this.isAuthenticated = false; // Clear sensitive state
        this.navigationContext = null;
        
        return List.of(event);
    }

    // Setters for test simulation of invalid states
    public void setLastActivityAt(Instant time) {
        this.lastActivityAt = time;
    }

    public void setNavigationContext(String context) {
        this.navigationContext = context;
    }
}
