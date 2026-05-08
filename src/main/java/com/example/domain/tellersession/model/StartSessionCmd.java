package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a teller session.
 * Immutable record carrying the necessary data to start a session.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean authenticated,
        String navigationState,
        Instant requestedAt
) implements Command {

    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (requestedAt == null) {
            requestedAt = Instant.now();
        }
    }
}