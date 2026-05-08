package com.example.domain.tellersession.command;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a Teller Session.
 * Record type for immutable data transfer.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean authenticated, // represents the successful authentication check
        Instant occurredAt
) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId required");
        Objects.requireNonNull(tellerId, "tellerId required");
        Objects.requireNonNull(terminalId, "terminalId required");
        // occurredAt defaults to now if null, but we enforce non-null for domain consistency
        if (occurredAt == null) occurredAt = Instant.now();
    }
}
