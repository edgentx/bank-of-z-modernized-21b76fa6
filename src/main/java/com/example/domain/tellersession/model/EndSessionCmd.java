package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to terminate a Teller Session.
 */
public record EndSessionCmd(String sessionId, Instant occurredAt) implements Command {
    public EndSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
