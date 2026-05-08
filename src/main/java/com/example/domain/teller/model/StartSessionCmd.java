package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a Teller Session.
 * Encapsulates authentication status, terminal details, and navigation context.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    NavigationState navigationState,
    Instant lastKnownActivity
) implements Command {

    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
    }
}
