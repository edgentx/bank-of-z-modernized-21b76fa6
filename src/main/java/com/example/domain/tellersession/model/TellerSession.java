package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Teller Session Aggregate.
 * Story: S-18 Implement StartSessionCmd.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean isAuthenticated; // Invariant: Authenticated
    private Instant lastActivityAt; // Invariant: Timeout
    private String currentContext;  // Invariant: Nav State
    private boolean started = false;

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
        // AC: A teller must be authenticated to initiate a session.
        // We assume the command implies a successful auth attempt, but the AC says "A teller MUST be authenticated".
        // We will assume the presence of a tellerId in the cmd implies a valid auth attempt for this context.
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalStateException("Teller must be authenticated (TellerId missing).");
        }

        // AC: Sessions must timeout after a configured period of inactivity.
        if (cmd.timeout() == null || cmd.timeout().isNegative() || cmd.timeout().isZero()) {
            throw new IllegalArgumentException("Session timeout configuration must be a positive duration.");
        }

        // AC: Navigation state must accurately reflect the current operational context.
        if (cmd.currentContext() == null || cmd.currentContext().isBlank()) {
            throw new IllegalArgumentException("Current operational context (Navigation State) is required.");
        }

        // Aggregate state transition
        this.isAuthenticated = true;
        this.currentContext = cmd.currentContext();
        this.lastActivityAt = Instant.now();
        this.started = true;

        // Emit Event
        // Fix: Added sourceChannelId and currentContext to match Record signature
        var event = new SessionStartedEvent(
            cmd.sessionId(),
            cmd.tellerId(),
            cmd.terminalId(),
            cmd.sourceChannelId(), // Was missing, causing error
            cmd.currentContext(),  // Was missing, causing error
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Getters for testing/querying
    public boolean isStarted() { return started; }
    public String getCurrentContext() { return currentContext; }
}
