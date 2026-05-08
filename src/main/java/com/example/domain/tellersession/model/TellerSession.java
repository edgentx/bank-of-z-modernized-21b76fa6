package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * TellerSession aggregate handling UI state, lifecycle, and sensitive data clearing.
 * Enforces invariants around authentication, timeouts, and navigation context.
 */
public class TellerSession extends AggregateRoot {
    private final UUID sessionId;
    private final String tellerId;
    private boolean authenticated;
    private boolean active;
    private Instant lastActivityAt;
    private String navigationState;

    // Configuration for timeout (default 15 minutes for demo)
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    // Constructor for creating a new session (via reflection or factory)
    public TellerSession(UUID sessionId, String tellerId) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.active = false; // Requires explicit activation/auth
        this.authenticated = false;
        this.lastActivityAt = Instant.now();
    }

    @Override
    public String id() {
        return sessionId.toString();
    }

    // --- Invariants & Helper Methods for Testing ---

    public void markAuthenticated() {
        this.authenticated = true;
        this.active = true;
        this.lastActivityAt = Instant.now();
        this.navigationState = "/HOME";
    }

    public void markInactive() {
        this.active = false;
    }

    public void markTimedOut() {
        this.active = false;
        // Set last activity far in the past to simulate timeout
        this.lastActivityAt = Instant.now().minus(SESSION_TIMEOUT).minusSeconds(1);
    }

    public void setNavigationState(String state) {
        this.navigationState = state;
    }

    private void checkInvariants() {
        // 1. Authenticated
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }
        // 2. Timeout
        if (Duration.between(lastActivityAt, Instant.now()).compareTo(SESSION_TIMEOUT) > 0) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }
        // 3. Navigation State (Simple check: must not be null/blank if active)
        if (navigationState == null || navigationState.isBlank()) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }
    }

    // --- Command Handling ---

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd) {
            return handleEndSession();
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleEndSession() {
        // Invariants are checked before executing the command logic
        checkInvariants();

        if (!active) {
            // Depending on strictness, ending an inactive session might be idempotent or an error.
            // Given the prompt implies rejection on invariant violation, we assume
            // if we reach here, invariants passed, but logical state prevents action.
            // However, ending a session is usually idempotent.
            // But let's assume if invariants fail, we error. If invariants pass, we end.
            // If the session is already inactive, no event needed or error?
            // Let's treat it as an idempotent success if invariants pass, or strictly only active.
            // For this exercise, we assume the session IS active and valid to end.
        }

        var event = new SessionEndedEvent(this.sessionId, Instant.now());
        this.active = false;
        this.authenticated = false; // Clear sensitive state
        this.navigationState = null;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}
