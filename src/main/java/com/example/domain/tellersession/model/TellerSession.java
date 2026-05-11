package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.time.Duration;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean isAuthenticated = true;

    // Invariant enforcement constants
    private static final long SESSION_TIMEOUT_SECONDS = 300; // 5 minutes

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
    }

    // Method for test setup to simulate invariant violation
    public void markAsUnauthenticated() {
        this.isAuthenticated = false;
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
        if (!isAuthenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // We enforce this by checking if the command timestamp is too old (stale)
        Instant now = Instant.now();
        if (cmd.timestamp() != null) {
            Duration duration = Duration.between(cmd.timestamp(), now);
            if (duration.abs().getSeconds() > SESSION_TIMEOUT_SECONDS) {
                throw new IllegalArgumentException("Sessions must timeout after a configured period of inactivity.");
            }
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        if (cmd.navigationState() == null || cmd.navigationState().isBlank()) {
            throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context.");
        }
        // Assuming valid state for this story context (INIT, MENU, etc.)
        if (!cmd.navigationState().equals("INIT")) {
             throw new IllegalArgumentException("Navigation state must accurately reflect the current operational context.");
        }

        // Success path
        SessionStartedEvent event = new SessionStartedEvent(
            this.sessionId,
            cmd.tellerId(),
            cmd.terminalId(),
            cmd.navigationState(),
            Instant.now()
        );
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}
