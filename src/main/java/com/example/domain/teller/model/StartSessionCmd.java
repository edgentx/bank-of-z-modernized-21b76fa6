package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a Teller Session.
 * Requires authentication proof (represented here by valid non-null IDs and a valid timeout).
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant sessionTimeoutAt
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
        Objects.requireNonNull(sessionTimeoutAt, "sessionTimeoutAt cannot be null");
        if (sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be blank");
        if (tellerId.isBlank()) throw new IllegalArgumentException("tellerId cannot be blank");
        if (terminalId.isBlank()) throw new IllegalArgumentException("terminalId cannot be blank");
        if (sessionTimeoutAt.isBefore(Instant.now())) throw new IllegalArgumentException("sessionTimeoutAt must be in the future");
    }
}