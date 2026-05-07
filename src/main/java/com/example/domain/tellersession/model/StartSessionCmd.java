package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to initiate a teller session.
 * Ensures valid inputs for teller and terminal context.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements Command {

    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or blank");
        }
        if (terminalId == null || terminalId.isBlank()) {
            throw new IllegalArgumentException("terminalId cannot be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }
    }

    // Convenience constructor for tests where ID generation is acceptable
    public StartSessionCmd(String tellerId, String terminalId) {
        this(UUID.randomUUID().toString(), tellerId, terminalId, Instant.now());
    }
}
