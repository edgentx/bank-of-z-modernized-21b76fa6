package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current teller session.
 * Used to clear sensitive state and release locks.
 */
public record EndSessionCmd(String sessionId, String reason) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
