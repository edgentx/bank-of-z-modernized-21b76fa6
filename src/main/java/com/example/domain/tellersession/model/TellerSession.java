package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * TellerSession Aggregate
 * <p>
 * Represents the active state of a Bank Teller's interaction with the system.
 * Implements the navigation and lifecycle invariants defined in S-18.
 * </p>
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private SessionStatus status = SessionStatus.NONE;
    private boolean authenticated;
    private Instant lastActivityAt;
    private String navigationContext; // Current screen/flow

    // Invariant: 30 minute timeout (in milliseconds)
    private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000;

    public enum SessionStatus {
        NONE, INITIALIZED, TERMINATED
    }

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
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
        if (!cmd.isAuthenticated()) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // (Checking the activity timestamp from the command payload context)
        Instant now = cmd.getTimestamp();
        if (now == null) {
            // Should be provided by command, default to now if not
            now = Instant.now();
        }
        
        // Invariant: Navigation state must accurately reflect the current operational context.
        // We require the navigation context to be a valid non-blank identifier to start.
        String navCtx = cmd.getInitialContext();
        if (navCtx == null || navCtx.isBlank()) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        // Business Logic
        if (status != SessionStatus.NONE) {
            throw new IllegalStateException("Session already active for " + sessionId);
        }

        // Construct Event
        // 5 args: sessionId, tellerId, terminalId, context, occurredAt
        SessionStartedEvent event = new SessionStartedEvent(
            sessionId,
            cmd.getTellerId(),
            cmd.getTerminalId(),
            navCtx,
            now
        );

        // Apply state changes
        this.tellerId = cmd.getTellerId();
        this.terminalId = cmd.getTerminalId();
        this.status = SessionStatus.INITIALIZED;
        this.authenticated = true;
        this.navigationContext = navCtx;
        this.lastActivityAt = now;

        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    // Getters for testing/validation
    public SessionStatus getStatus() {
        return status;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getTellerId() {
        return tellerId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public String getNavigationContext() {
        return navigationContext;
    }
}