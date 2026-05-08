package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Aggregate for Teller Session S-20.
 * Represents the teller's terminal state, navigation context, and security boundaries.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private Instant lastActivityAt;
    private String navigationState; // e.g., "CUSTOMER_SEARCH", "ACCOUNT_DETAIL"
    private boolean active = false;

    // Configuration: Session Timeout (e.g., 15 minutes)
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String id() {
        return sessionId;
    }

    /**
     * Helper method for testing to bypass complex 'StartSession' logic if needed,
     * or used by the 'StartSession' command (not in scope for S-20, but implied).
     */
    public void initializeState(String tellerId, Instant lastActivityAt, String navigationState) {
        this.tellerId = tellerId;
        this.lastActivityAt = lastActivityAt;
        this.navigationState = navigationState;
        this.active = true;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return handleEndSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleEndSession(EndSessionCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        // (To end a session, the session must exist and have an authenticated teller)
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalStateException("Cannot end session: No authenticated teller associated with this session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // Check if the session has already expired before allowing explicit End.
        // (Even if we are ending it, we enforce consistency on the state)
        if (lastActivityAt != null) {
            Duration inactive = Duration.between(lastActivityAt, cmd.endedAt());
            if (inactive.compareTo(SESSION_TIMEOUT) > 0) {
                throw new IllegalStateException("Cannot end session: Session has already timed out due to inactivity.");
            }
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // (Check for valid state)
        if (navigationState == null || navigationState.isBlank() || navigationState.equals("INVALID_STATE_CTX")) {
            throw new IllegalStateException("Cannot end session: Navigation state is invalid or corrupted (" + navigationState + ").");
        }

        // Apply state changes
        this.active = false;
        this.tellerId = null; // Clear sensitive state
        this.navigationState = null;

        // Create Event
        // Constructor args: aggregateId, type, occurredAt
        SessionEndedEvent event = new SessionEndedEvent(
                this.sessionId,
                "teller.session.ended",
                cmd.endedAt()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}
