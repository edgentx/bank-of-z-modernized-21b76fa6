package com.example.domain.tellermessaging.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * TellerSession Aggregate
 * Handles user interface navigation and session state for the teller terminal.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean authenticated = false; // Invariant: Must be true to end session (contextual requirement)
    private Instant lastActivityAt;
    private boolean isActive = true;

    // Constants for Invariants
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    // Navigation state flags (mocked for context)
    private boolean navigationStateValid = true; 

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now(); // Defaults to now for new session
    }

    /**
     * Test helpers to simulate the violations described in the Gherkin scenarios.
     * In a real persistence scenario, this state would be loaded from the DB.
     */
    public void markUnauthenticated() { this.authenticated = false; }
    public void markAuthenticated() { this.authenticated = true; }
    public void markStale() { this.lastActivityAt = Instant.now().minus(SESSION_TIMEOUT.plusSeconds(60)); }
    public void markNavigationInvalid() { this.navigationStateValid = false; }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return endSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // --- Invariant Enforcement ---

        // Rule: A teller must be authenticated to initiate a session (implies session must be active/auth'd)
        // Interpretation: You can't end a session that isn't properly yours or established.
        if (!authenticated) {
            throw new IllegalStateException("Teller must be authenticated to end session.");
        }

        // Rule: Sessions must timeout after a configured period of inactivity.
        // Note: Usually 'Timeout' is an auto-process, but if a command comes in for a stale session,
        // we might reject it as a domain rule (e.g., 'Session Expired').
        // Or, 'EndSessionCmd' might be the cleanup action. If the requirement is strict rejection:
        if (isTimedOut()) {
            throw new IllegalStateException("Session has timed out due to inactivity.");
        }

        // Rule: Navigation state must accurately reflect the current operational context.
        if (!navigationStateValid) {
            throw new IllegalStateException("Navigation state is invalid for current operational context.");
        }

        // --- Execution ---
        SessionEndedEvent event = new SessionEndedEvent(this.sessionId);
        this.isActive = false;
        this.authenticated = false; // Clear sensitive state
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean isTimedOut() {
        if (lastActivityAt == null) return true;
        return Duration.between(lastActivityAt, Instant.now()).compareTo(SESSION_TIMEOUT) > 0;
    }

    // Getters for testing/validation
    public boolean isActive() { return isActive; }
}
