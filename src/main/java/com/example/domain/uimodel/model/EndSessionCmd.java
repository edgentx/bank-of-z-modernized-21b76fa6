package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to end a Teller Session.
 * Validates that the provided sessionId matches the aggregate context.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
