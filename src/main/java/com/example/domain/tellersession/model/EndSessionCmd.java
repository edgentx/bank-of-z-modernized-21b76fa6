package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.UUID;

/**
 * Command to end a teller session.
 * BANK S-20 — Teller Session Termination.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
