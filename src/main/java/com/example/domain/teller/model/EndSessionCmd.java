package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to end a current Teller session.
 */
public record EndSessionCmd(String sessionId, Instant occurredAt) implements Command {
    public EndSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        // OccurredAt can be defaulted to now if null, but enforcing explicit is safer for event sourcing
    }
}
