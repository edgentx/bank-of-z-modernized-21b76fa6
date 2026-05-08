package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an existing teller session.
 * Useful for 3270 F3 (Exit) or timeout handlers.
 */
public record EndSessionCmd(String sessionId, String tellerId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
    }
}
