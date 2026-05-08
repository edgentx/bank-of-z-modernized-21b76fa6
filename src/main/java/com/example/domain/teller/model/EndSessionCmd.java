package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to end a Teller Session.
 * Required to clear sensitive state and terminate the user interface navigation context.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
