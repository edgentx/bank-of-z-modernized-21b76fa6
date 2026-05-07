package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Context string is used for testing invariants (auth, nav state, etc)
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, String context) implements Command {
    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
    }

    // Constructor for standard happy path
    public StartSessionCmd(String sessionId, String tellerId, String terminalId) {
        this(sessionId, tellerId, terminalId, "valid");
    }
}
