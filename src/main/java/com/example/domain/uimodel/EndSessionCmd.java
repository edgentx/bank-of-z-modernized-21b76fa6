package com.example.domain.uimodel;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to terminate an active Teller Session.
 * Clears sensitive state and emits a SessionEndedEvent.
 */
public record EndSessionCmd(String sessionId, String reason) implements Command {

    public EndSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        if (sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be blank");
        // Reason is optional, but if provided, shouldn't be blank
        if (reason != null && reason.isBlank()) {
            throw new IllegalArgumentException("reason cannot be blank if provided");
        }
    }
}
