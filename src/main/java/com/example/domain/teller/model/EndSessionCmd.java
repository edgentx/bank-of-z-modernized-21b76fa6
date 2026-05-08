package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a Teller Session.
 * Sensitive state should be cleared upon execution.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}