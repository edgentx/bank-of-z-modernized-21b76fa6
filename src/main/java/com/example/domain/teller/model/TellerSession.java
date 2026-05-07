package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {
    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private String navState;
    private boolean active;
    private Instant lastActivityAt;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.navState = "NONE";
        this.active = false;
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
        if (active) {
            throw new IllegalStateException("Session already active: " + sessionId);
        }

        // Invariant: Authentication check (tellerId presence acts as auth token for this scope)
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Timeout validation
        if (cmd.timeout() == null || cmd.timeout().isNegative() || cmd.timeout().isZero()) {
            throw new IllegalArgumentException("Sessions must timeout after a configured period of inactivity.");
        }

        // Invariant: Navigation state (Operational Context)
        // Assuming default context is valid. If cmd included a specific state, we would validate it.
        String initialNavState = "HOME";
        if (!isValidContext(initialNavState)) {
             throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context.");
        }

        SessionStartedEvent event = new SessionStartedEvent(
            cmd.sessionId(),
            cmd.tellerId(),
            cmd.terminalId(),
            cmd.occurredAt()
        );

        this.tellerId = cmd.tellerId();
        this.terminalId = cmd.terminalId();
        this.navState = initialNavState;
        this.active = true;
        this.lastActivityAt = cmd.occurredAt();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private boolean isValidContext(String context) {
        return context != null && !context.isBlank();
    }
}
