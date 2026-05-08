package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant initiatedAt,
        boolean authenticated // Simulating security context check flag for testing
) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId required");
        Objects.requireNonNull(tellerId, "tellerId required");
        Objects.requireNonNull(terminalId, "terminalId required");
    }

    // Convenience constructor for tests to simulate 'authenticated' state vs 'unauthenticated'
    public static StartSessionCmd authenticated(String sessionId, String tellerId, String terminalId, Instant when) {
        return new StartSessionCmd(sessionId, tellerId, terminalId, when, true);
    }

    public static StartSessionCmd unauthenticated(String sessionId, String tellerId, String terminalId, Instant when) {
        return new StartSessionCmd(sessionId, tellerId, terminalId, when, false);
    }
}
