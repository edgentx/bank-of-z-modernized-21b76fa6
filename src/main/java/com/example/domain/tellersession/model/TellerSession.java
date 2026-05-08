package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * TellerSession Aggregate Root.
 * Manages the lifecycle of a teller's authenticated UI session.
 * Expects TellerSessionRepository to resolve via ID.
 */
public class TellerSession extends AggregateRoot {

    // Navigational State Constants
    private static final String STATE_IDLE = "IDLE";
    private static final String STATE_TRANSACTION = "TRANSACTION"; 
    private static final String STATE_INQUIRY = "INQUIRY";

    // Invariants
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    private final String sessionId;
    private String tellerId;
    private Instant lastActivityAt;
    private String currentState; 
    private boolean authenticated;
    private boolean active;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.currentState = STATE_IDLE;
        this.active = false; // Sessions must be explicitly started or implied by creation
        this.authenticated = false;
    }

    @Override
    public String id() {
        return sessionId;
    }

    // Setter for test setup (Given steps)
    public void markAuthenticated(String tellerId) {
        this.tellerId = tellerId;
        this.authenticated = true;
        this.active = true;
        this.lastActivityAt = Instant.now();
    }

    public void setLastActivityAt(Instant time) {
        this.lastActivityAt = time;
    }

    public void setNavigationState(String state) {
        this.currentState = state;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd) {
            return handleEndSession((EndSessionCmd) cmd);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleEndSession(EndSessionCmd cmd) {
        // 1. Invariant: A teller must be authenticated to initiate a session (Ending implies one was initiated/auth'd)
        // Scenario 2: Rejected if not authenticated.
        if (!authenticated) {
            throw new IllegalStateException("Teller must be authenticated to end a session.");
        }

        // 2. Invariant: Sessions must timeout after a configured period of inactivity.
        // Scenario 3: Rejected if already timed out (invalid operation).
        if (isSessionExpired()) {
            throw new IllegalStateException("Session has expired due to inactivity.");
        }

        // 3. Invariant: Navigation state must accurately reflect the current operational context.
        // Scenario 4: Rejected if state is invalid (e.g., mid-transaction dirty state).
        // Assuming IDLE is the only safe state to terminate cleanly without explicit 'Cancel' logic.
        if (!STATE_IDLE.equals(currentState)) {
            throw new IllegalStateException(
                String.format("Cannot end session. Navigation state must be IDLE, currently: %s", currentState)
            );
        }

        // Scenario 1: Success.
        SessionEndedEvent event = new SessionEndedEvent(this.sessionId, Instant.now());
        
        // Apply state changes
        this.active = false;
        this.authenticated = false;
        this.tellerId = null; // Clear sensitive state
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean isSessionExpired() {
        if (lastActivityAt == null) return true;
        return Instant.now().isAfter(lastActivityAt.plus(SESSION_TIMEOUT));
    }
}
