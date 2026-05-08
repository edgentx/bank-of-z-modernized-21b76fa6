package com.example.domain.tellerm_session.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * Validates the session ID matches the aggregate being commanded.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
