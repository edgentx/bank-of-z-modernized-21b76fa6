package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String activeTellerId;
    private String activeTerminalId;
    private Instant lastActivityAt;
    private boolean isActive;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.isActive = false;
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
        if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
            throw new IllegalArgumentException("Teller must be authenticated.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // Assuming the command carries the desired timeout. If the timeout is in the past, reject.
        if (cmd.sessionTimeoutAt() != null && cmd.sessionTimeoutAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Session timeout configuration invalid.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // Assuming the command carries the expected navigation state. If it conflicts, reject.
        if (cmd.expectedNavigationState() != null && !cmd.expectedNavigationState().equals("IDLE")) {
            throw new IllegalStateException("Navigation state must be IDLE to start a session.");
        }

        if (this.isActive) {
            throw new IllegalStateException("Session already active.");
        }

        var event = new SessionStartedEvent(
                cmd.sessionId(),
                cmd.tellerId(),
                cmd.terminalId(),
                Instant.now()
        );

        this.activeTellerId = cmd.tellerId();
        this.activeTerminalId = cmd.terminalId();
        this.lastActivityAt = Instant.now();
        this.isActive = true;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}
