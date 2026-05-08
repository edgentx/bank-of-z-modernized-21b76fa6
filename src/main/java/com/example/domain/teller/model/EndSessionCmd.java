package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to end a Teller Session.
 * S-20: Terminates the teller session and clears sensitive session state.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        if (sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be blank");
    }
}
