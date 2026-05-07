package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current TellerSession.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
