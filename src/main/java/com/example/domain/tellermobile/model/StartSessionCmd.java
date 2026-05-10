package com.example.domain.tellermobile.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 * Requires a valid, authenticated teller ID and a terminal ID.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId required");
        Objects.requireNonNull(tellerId, "tellerId required");
        Objects.requireNonNull(terminalId, "terminalId required");
        Objects.requireNonNull(occurredAt, "occurredAt required");
    }
}
