package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to initiate a teller session.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    Instant sessionTimeout
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
        // sessionTimeout can be in the past for testing invariants, so no validation here
    }
}
