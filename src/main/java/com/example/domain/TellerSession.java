package com.example.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TellerSession {
    private final UUID id;
    private final List<Object> events = new ArrayList<>();
    private int version = 0;

    // Invariants / State
    private boolean isAuthenticated;

    private TellerSession(UUID id) {
        this.id = id;
        this.isAuthenticated = false; // Initial state
    }

    public static TellerSession create() {
        return new TellerSession(UUID.randomUUID());
    }

    public Object execute(StartSessionCmd cmd) throws DomainError {
        validateCommand(cmd);

        // Apply business logic
        SessionStartedEvent event = new SessionStartedEvent(
                this.id,
                cmd.getTellerId(),
                cmd.getTerminalId(),
                Instant.now(),
                cmd.getTimeoutConfig()
        );

        // Apply event to state (In-memory)
        apply(event);

        return event;
    }

    private void validateCommand(StartSessionCmd cmd) throws DomainError {
        if (cmd.getTellerId() == null || cmd.getTellerId().isBlank()) {
            throw new DomainError("A teller must be authenticated to initiate a session.");
        }

        if (cmd.getTerminalId() == null || cmd.getTerminalId().isBlank()) {
            throw new DomainError("A valid terminal ID is required.");
        }

        if (cmd.getTimeoutConfig() == null || cmd.getTimeoutConfig().isNegative()) {
            throw new DomainError("Sessions must timeout after a configured period of inactivity.");
        }

        // Validate Navigation/Context state
        if ("INVALID_CONTEXT".equals(cmd.getInitialContext())) {
            throw new DomainError("Navigation state must accurately reflect the current operational context.");
        }
    }

    private void apply(SessionStartedEvent event) {
        this.isAuthenticated = true;
        this.events.add(event);
        this.version++;
    }

    public UUID getId() {
        return id;
    }

    public List<Object> getUncommittedEvents() {
        return List.copyOf(events);
    }
}
