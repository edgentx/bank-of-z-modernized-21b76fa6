package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to initiate a new teller session on a specific terminal.
 * Represents the user's intent to log in to the Teller UI.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
        if (sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be blank");
        if (tellerId.isBlank()) throw new IllegalArgumentException("tellerId cannot be blank");
        if (terminalId.isBlank()) throw new IllegalArgumentException("terminalId cannot be blank");
    }
}
