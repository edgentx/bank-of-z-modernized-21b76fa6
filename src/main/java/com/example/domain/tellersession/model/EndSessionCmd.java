package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to end an active Teller Session.
 * Part of S-20.
 */
public record EndSessionCmd(String sessionId, String reason) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        // Reason is optional but good practice
    }
}
