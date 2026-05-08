package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * TellerSession Aggregate.
 * Manages the lifecycle of a Bank Teller's terminal session, including authentication,
 * timeout monitoring, and navigation state integrity.
 * 
 * Aggregates:
 * - TellerSession (Root)
 * Commands:
 * - EndSessionCmd
 * Events:
 * - SessionEndedEvent
 */
public class TellerSession extends AggregateRoot {

    private final UUID sessionId;
    private final String tellerId;
    private Instant lastActivity;
    private final Duration timeoutDuration;
    private String navigationState;
    private boolean active = false;

    /**
     * Constructor for creating a new session (typically used by a StartSessionCmd, not implemented here but implied).
     * For the purposes of this feature (S-20), we assume the session is already active.
     */
    public TellerSession(UUID sessionId, String tellerId, Instant lastActivity, Duration timeoutDuration) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.lastActivity = lastActivity;
        this.timeoutDuration = timeoutDuration;
        // Assume if created with these details, it is in an active IDLE state unless specified otherwise.
        this.active = true;
        this.navigationState = "IDLE"; 
    }

    @Override
    public String id() {
        return sessionId.toString();
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return endSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // Invariant 1: Authentication Check
        if (this.tellerId == null || this.tellerId.isBlank()) {
            throw new IllegalStateException("Teller must be authenticated to end session.");
        }

        // Invariant 2: Timeout Check (Session must not already be timed out)
        Instant now = Instant.now();
        Duration inactiveDuration = Duration.between(this.lastActivity, now);
        
        // Allow ending the session, but reject if it has *already* expired?
        // The acceptance criteria says: "Sessions must timeout after a configured period of inactivity."
        // Usually, this means you can't act on a dead session, OR it means the system forces a timeout.
        // The test expects: "EndSessionCmd rejected — Sessions must timeout... Given a TellerSession aggregate that violates: Sessions must timeout..."
        // This implies we reject the command if the session is currently in a timed-out state.
        if (inactiveDuration.compareTo(this.timeoutDuration) > 0) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // Invariant 3: Navigation State Consistency
        if (this.navigationState == null || this.navigationState.isBlank() || "UNKNOWN_CONTEXT".equals(this.navigationState)) {
            throw new IllegalStateException("Navigation state is invalid or unknown. Cannot safely end session.");
        }

        // Apply state changes
        this.active = false;
        // Clear sensitive session state as requested in Story Description
        this.navigationState = null; 
        // Note: We do not nullify tellerId or sessionId as they are identity fields useful for audit trails in the event.

        SessionEndedEvent event = new SessionEndedEvent(this.sessionId, this.tellerId, now);
        addEvent(event);
        incrementVersion();
        
        return List.of(event);
    }

    // Getter required for test setup (though tests usually instantiate directly, keeping logic encapsulated)
    public void setNavigationState(String state) {
        this.navigationState = state;
    }

    public boolean isActive() {
        return active;
    }

    public String getTellerId() {
        return tellerId;
    }
}