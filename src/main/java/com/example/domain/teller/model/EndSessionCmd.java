package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.UUID;

/**
 * Command to terminate a Teller Session.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
