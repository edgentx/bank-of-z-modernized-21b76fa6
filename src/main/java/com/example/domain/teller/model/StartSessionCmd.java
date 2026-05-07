package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a new teller session.
 * Bound to TellerSession aggregate.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant authenticatedAt
) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
        Objects.requireNonNull(authenticatedAt, "authenticatedAt cannot be null");
    }
}
