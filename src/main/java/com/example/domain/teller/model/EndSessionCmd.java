package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to end a teller session.
 * Part of S-20: EndSessionCmd on TellerSession.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
