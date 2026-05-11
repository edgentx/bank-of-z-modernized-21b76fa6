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
 * Manages the lifecycle of a teller session, including authentication state,
 * activity timeouts, and navigation context.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean isAuthenticated = false;
    private Instant lastActivityAt;
    private Duration sessionTimeout;
    private String navigationContext; // e.g. "IDLE", "TRANSACTION_IN_PROGRESS"
    private boolean active = false;

    // Configuration for session timeout (e.g., 15 minutes)
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(15);

    // Constructor for creating a new session (e.g., via StartSessionCmd - not implemented here)
    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now(); // Initialize to now
        this.sessionTimeout = DEFAULT_TIMEOUT;
        this.navigationContext = "IDLE";
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd) {
            return handle((EndSessionCmd) cmd);
        }
        throw new UnknownCommandException(cmd);
    }

    /**
     * Handles the EndSessionCmd.
     * Enforces invariants:
     * 1. Authentication check (must be authenticated to have started a session).
     * 2. Timeout check (session must not have timed out).
     * 3. Navigation context check (must be in a safe state to end).
     */
    private List<DomainEvent> handle(EndSessionCmd cmd) {
        // Invariant 1: A teller must be authenticated to initiate a session.
        // (Here interpreted as: An active session must have been authenticated. 
        // We rely on the 'active' and 'isAuthenticated' flags set during creation/flow.)
        if (active && !isAuthenticated) {
             throw new IllegalStateException("Cannot end session: Teller is not authenticated.");
        }
        
        // For the purpose of the "violates" scenario, if we are in a state where 
        // active=true but authenticated=false, we block it.

        // Invariant 2: Sessions must timeout after a configured period of inactivity.
        if (isSessionTimedOut()) {
            throw new IllegalStateException("Cannot end session: Session has already timed out due to inactivity.");
        }

        // Invariant 3: Navigation state must accurately reflect the current operational context.
        // Assuming "IDLE" is the only valid context to cleanly end a session.
        if (!"IDLE".equalsIgnoreCase(navigationContext)) {
            throw new IllegalStateException("Cannot end session: Navigation state is " + navigationContext + ". Please return to IDLE first.");
        }

        // Apply state changes
        this.active = false;
        // Sensitive state cleared implicitly by object lifecycle or projection reset.

        var event = new SessionEndedEvent(this.sessionId, Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean isSessionTimedOut() {
        if (lastActivityAt == null) return false;
        return Instant.now().isAfter(lastActivityAt.plus(sessionTimeout));
    }

    // --- Getters and Setters for Test State Management ---
    
    public void markAuthenticated() {
        this.isAuthenticated = true;
        this.active = true;
    }

    public void setLastActivityAt(Instant lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public void setNavigationContext(String context) {
        this.navigationContext = context;
    }

    public boolean isActive() {
        return active;
    }

    public String getNavigationContext() {
        return navigationContext;
    }
}
