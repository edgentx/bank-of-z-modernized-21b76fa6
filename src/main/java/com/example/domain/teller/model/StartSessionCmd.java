package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a teller session.
 * AC: Requires authentication, valid timeout, and valid navigation context (terminal ID).
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Instant sessionTimeoutAt
) implements Command {

    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Session ID cannot be blank");
        }
    }
}
