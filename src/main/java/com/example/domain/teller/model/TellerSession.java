package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * Aggregate Root for Teller Session management.
 * Handles lifecycle of a teller's interaction with the terminal (S-18).
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String authenticatedTellerId;
    private String currentTerminalId;
    private SessionState state;
    private Instant lastActivityAt;
    private Instant sessionStartedAt;

    public enum SessionState {
        NONE,
        ACTIVE,
        TIMED_OUT
    }

    // Duration in minutes before a session times out (Configurable, hardcoded for this domain exercise)
    private static final long SESSION_TIMEOUT_MINUTES = 15;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.state = SessionState.NONE;
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof StartSessionCmd c) {
            return startSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> startSession(StartSessionCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        // Note: In a real app, the 'cmd' might carry a token. Here we assume presence of tellerId implies auth
        // or the context has been validated upstream. For this scenario, we verify ID is present.
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("Teller must be authenticated (TellerID missing).");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // If a session is already active, check if it has timed out before rejecting a new start request.
        if (this.state == SessionState.ACTIVE) {
            if (hasTimedOut()) {
                // Force transition to timeout state logically to allow a fresh start or reject appropriately.
                // For S-18 specific rejection requirement, we check strictly.
                throw new IllegalStateException("Session must timeout after a configured period of inactivity.");
            } else {
                throw new IllegalStateException("Session already active for this terminal/teller context.");
            }
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // We interpret this as ensuring the terminal ID is valid and provided.
        if (cmd.terminalId() == null || cmd.terminalId().isBlank()) {
            throw new IllegalArgumentException("Navigation state invalid (Terminal ID required).");
        }

        // Apply state changes
        this.authenticatedTellerId = cmd.tellerId();
        this.currentTerminalId = cmd.terminalId();
        this.state = SessionState.ACTIVE;
        this.lastActivityAt = Instant.now();
        this.sessionStartedAt = Instant.now();

        var event = new SessionStartedEvent(this.sessionId, this.authenticatedTellerId, this.currentTerminalId, Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean hasTimedOut() {
        if (this.lastActivityAt == null) return false;
        return Instant.now().isAfter(this.lastActivityAt.plusSeconds(SESSION_TIMEOUT_MINUTES * 60));
    }

    // Getters for testing
    public String getAuthenticatedTellerId() { return authenticatedTellerId; }
    public String getCurrentTerminalId() { return currentTerminalId; }
    public SessionState getState() { return state; }
}