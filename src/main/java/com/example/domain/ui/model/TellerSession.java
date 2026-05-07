package com.example.domain.ui.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellerauthentication.model.TellerAuthenticatedEvent;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean authenticated = false;
    private Instant lastActivityAt;
    private boolean active = false;

    // Configuration constants (in real app injected)
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String id() {
        return sessionId;
    }

    /**
     * Internal helper to apply state changes from events for testing purposes.
     * In a full CQRS setup, this would be part of a generic 'apply' logic.
     */
    public void apply(TellerAuthenticatedEvent event) {
        this.authenticated = true;
        this.lastActivityAt = event.occurredAt();
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof StartSessionCmd c) {
            return startSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> startSession(StartSessionCmd cmd) {
        // Invariant 1: A teller must be authenticated to initiate a session.
        if (!authenticated) {
            throw new IllegalStateException("Teller must be authenticated to initiate a session.");
        }

        // Invariant 2: Sessions must timeout after a configured period of inactivity.
        // Assuming 'lastActivityAt' is the auth time if session hasn't started.
        if (lastActivityAt != null) {
            Duration idle = Duration.between(lastActivityAt, Instant.now());
            if (idle.compareTo(SESSION_TIMEOUT) > 0) {
                throw new IllegalStateException("Authentication has timed out.");
            }
        }

        // Invariant 3: Navigation state must accurately reflect the current operational context.
        // For this story, we enforce that the terminalId is valid (not null/blank) and
        // that we aren't trying to start an already active session.
        if (active) {
            throw new IllegalStateException("Navigation state error: Session is already active.");
        }
        if (cmd.terminalId() == null || cmd.terminalId().isBlank()) {
            throw new IllegalArgumentException("Terminal ID must be provided for accurate navigation context.");
        }

        // Business Logic
        var event = new SessionStartedEvent(cmd.sessionId(), cmd.tellerId(), cmd.terminalId(), Instant.now());
        this.active = true;
        this.lastActivityAt = event.occurredAt();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public boolean isActive() {
        return active;
    }
}
