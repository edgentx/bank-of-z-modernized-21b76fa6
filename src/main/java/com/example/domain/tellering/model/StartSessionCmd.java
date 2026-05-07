package com.example.domain.tellering.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    String initialState,
    Instant sessionTimeout,
    boolean isAuthenticated // Defaulted in secondary constructor, but explicit here for clarity in tests
) implements Command {
    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
    }

    // Convenience constructor assuming authenticated = true for standard path
    public StartSessionCmd(String sessionId, String tellerId, String terminalId, String initialState, Instant sessionTimeout) {
        this(sessionId, tellerId, terminalId, initialState, sessionTimeout, true);
    }
}