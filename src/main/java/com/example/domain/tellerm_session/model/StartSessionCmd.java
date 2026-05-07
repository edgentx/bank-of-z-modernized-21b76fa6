package com.example.domain.tellerm_session.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to start a new Teller Session.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant timestamp
) implements Command {
    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId required");
        }
        // tellerId and terminalId validation logic is handled by the Aggregate invariants.
    }

    // Convenience constructor that generates an ID and timestamp
    public StartSessionCmd(String tellerId, String terminalId) {
        this(UUID.randomUUID().toString(), tellerId, terminalId, Instant.now());
    }
}
