package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated,
    Instant lastActivityAt // Optional context for timeout check
) implements Command {

    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
    }

    // Convenience constructor for happy path defaults
    public StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean authenticated) {
        this(sessionId, tellerId, terminalId, authenticated, Instant.now());
    }
}